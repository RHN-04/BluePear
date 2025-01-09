package com.example.bluepear.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.bluepear.ui.canvas.DrawingAction
import com.example.bluepear.ui.canvas.Layer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private var _currentLine: Line? = null
    private val layers = mutableListOf<Layer>()
    private var currentLayerId: Int? = null
    private var nextLayerId = 1

    private var canvasWidth = 1080
    private var canvasHeight = 1920

    private val undoStack = mutableListOf<DrawingAction>()
    private val redoStack = mutableListOf<DrawingAction>()

    val currentLine: Line?
        get() = _currentLine

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    var currentBrushColor = floatArrayOf(0f, 0f, 0f, 1f)
    var currentBrushSize = 5f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)

        if (layers.isEmpty()) {
            addLayer()
        }
    }

    fun captureToBitmap(): Bitmap? {
        if (canvasWidth == 0 || canvasHeight == 0) {
            Log.w("MyGLRenderer", "Canvas size is not initialized. Width: $canvasWidth, Height: $canvasHeight")
            return null
        }

        Log.d("MyGLRenderer", "Starting captureToBitmap: Width=$canvasWidth, Height=$canvasHeight")

        val intBuffer = IntBuffer.allocate(canvasWidth * canvasHeight)

        try {
            GLES20.glFinish()
            Log.d("MyGLRenderer", "glFinish() called successfully")

            GLES20.glReadPixels(
                0, 0,
                canvasWidth, canvasHeight,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                intBuffer
            )
            Log.d("MyGLRenderer", "glReadPixels executed successfully")
        } catch (e: Exception) {
            Log.e("MyGLRenderer", "Error during glReadPixels: ${e.message}")
            return null
        }

        // Проверка ошибок OpenGL
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("MyGLRenderer", "OpenGL error during glReadPixels: $error")
            return null
        }

        // Получение данных пикселей и преобразование их в Bitmap
        val pixelData = intBuffer.array()
        val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)

        try {
            for (y in 0 until canvasHeight) {
                for (x in 0 until canvasWidth) {
                    val pixel = pixelData[(canvasHeight - y - 1) * canvasWidth + x] // Переворачиваем изображение

                    val flippedPixel = (pixel and -0x1000000) or  // Alpha
                            ((pixel and 0x00FF0000) shr 16) or  // Red
                            (pixel and 0x0000FF00) or  // Green
                            ((pixel and 0x000000FF) shl 16)  // Blue

                    bitmap.setPixel(x, y, flippedPixel)
                }
            }
            Log.d("MyGLRenderer", "Bitmap created successfully")
        } catch (e: Exception) {
            Log.e("MyGLRenderer", "Error during Bitmap creation: ${e.message}")
            return null
        }

        return bitmap
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        canvasWidth = width
        canvasHeight = height

        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio * 0.7f, ratio * 0.7f, -0.1f, 1f, 3f, 7f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        layers.forEach { layer ->
            if (layer.isVisible) {
                layer.draw(mvpMatrix)
            }
        }

        _currentLine?.draw(mvpMatrix)
    }

    fun setLayers(newLayers: List<Layer>) {
        layers.clear()
        layers.addAll(newLayers)
        currentLayerId = layers.firstOrNull()?.id
    }

    fun getLayerVisibility(id: Int): Boolean {
        val layer = layers.find { it.id == id }
        return layer?.isVisible ?: false
    }

    fun toggleLayerVisibility(id: Int, isVisible: Boolean) {
        val layer = layers.find { it.id == id }
        layer?.let {
            it.isVisible = isVisible
            Log.d("Renderer", "Layer ${layer.id} visibility set to $isVisible")
        }
    }

    fun drawLine(xStart: Float, yStart: Float, xEnd: Float, yEnd: Float) {
        val line = Line(currentBrushColor, currentBrushSize, layerId = currentLayerId ?: 1)
        line.addPoint(xStart, yStart)
        line.addPoint(xEnd, yEnd)

        val currentLayer = layers.find { it.id == currentLayerId } ?: return
        synchronized(currentLayer.linesToAdd) {
            currentLayer.linesToAdd.add(line)
        }

        line.draw(mvpMatrix)
    }

    fun startLine(x: Float, y: Float) {
        _currentLine = Line(currentBrushColor, currentBrushSize, layerId = currentLayerId ?: 1).also {
            it.addPoint(x, y)
            val currentLayer = layers.find { it.id == currentLayerId } ?: return
            synchronized(currentLayer.linesToAdd) {
                currentLayer.linesToAdd.add(it)
            }
        }
    }

    fun updateLine(x: Float, y: Float) {
        _currentLine?.addPoint(x, y)
    }

    fun clearCurrentLine() {
        _currentLine?.let {
            val action = DrawingAction.LineCompleted(it)
            undoStack.add(action)
            redoStack.clear()
            it.complete()

            val currentLayer = layers.find { it.id == currentLayerId } ?: return
            synchronized(currentLayer.lines) {
                currentLayer.lines.add(it)
            }

            _currentLine = null
        }
    }

    fun undoLastAction(action: DrawingAction) {
        val currentLayer = currentLayerId?.let { layers.getOrNull(it) } ?: return

        synchronized(currentLayer.lines) {
            when (action) {
                is DrawingAction.LineCompleted -> {
                    if (currentLayer.lines.isNotEmpty()) {
                        currentLayer.lines.removeAt(currentLayer.lines.size - 1)
                    }
                }
                else -> Unit
            }
        }
    }

    fun redoAction(action: DrawingAction) {
        val currentLayer = currentLayerId?.let { layers.getOrNull(it) } ?: return

        synchronized(currentLayer.lines) {
            when (action) {
                is DrawingAction.LineCompleted -> currentLayer.lines.add(action.line)
                else -> Unit
            }
        }
    }

    fun startEraser(x: Float, y: Float) {
        val currentLayer = layers.find { it.id == currentLayerId } ?: return
        val eraserRadius = currentBrushSize

        synchronized(currentLayer.lines) {
            val newLines = mutableListOf<Line>()
            for (line in currentLayer.lines) {
                if (!line.removeIntersectingPart(x, y, eraserRadius)) {
                    newLines.add(line)
                }
            }
            currentLayer.lines.clear()
            currentLayer.lines.addAll(newLines)
        }
    }

    fun removeLayer(id: Int) {
        val layerToRemove = layers.find { it.id == id }
        layerToRemove?.let {
            layers.remove(it)
            Log.d("Renderer", "Layer $id removed")

            if (currentLayerId == id) {
                currentLayerId = layers.lastOrNull()?.id
                Log.d("Renderer", "Current layer changed to $currentLayerId")
            }
        }
    }

    fun addLayer(): Int {
        val newLayer = Layer(id = nextLayerId++, program = MyGLProgram())
        layers.add(newLayer)
        currentLayerId = newLayer.id
        Log.d("Renderer", "Layer ${newLayer.id} added. Current layer: $currentLayerId")
        return newLayer.id
    }

    fun setActiveLayer(id: Int) {
        layers.find { it.id == id }?.let {
            currentLayerId = id
            Log.d("Renderer", "Active layer set to $currentLayerId")
        } ?: Log.w("Renderer", "Layer $id not found. Active layer unchanged.")
    }

    fun normalizeCoordinate(
        x: Float, y: Float, left: Float, top: Float, width: Float, height: Float
    ): Pair<Float, Float> {
        val normX = (x - left) / width * 2f - 1f
        val normY = 1f - (y - top) / height * 2f
        return normX to normY
    }
}