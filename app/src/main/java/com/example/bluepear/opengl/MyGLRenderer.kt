package com.example.bluepear.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.bluepear.ui.canvas.DrawingAction
import com.example.bluepear.ui.canvas.Layer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private var _currentLine: Line? = null
    private val layers = mutableListOf<Layer>()
    private var currentLayerIndex = 0

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

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio * 0.7f, ratio * 0.7f, -0.1f, 1f, 3f, 7f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        for (layer in layers) {
            if (layer.isVisible) {
                layer.draw(mvpMatrix)
            }
        }

        _currentLine?.let {
            it.draw(mvpMatrix)
        }
    }


    fun startLine(x: Float, y: Float) {
        _currentLine = Line(currentBrushColor, currentBrushSize).also {
            it.addPoint(x, y)
            synchronized(layers[currentLayerIndex].linesToAdd) {
                layers[currentLayerIndex].linesToAdd.add(it)
            }
        }
    }

    fun updateLine(x: Float, y: Float) {
        _currentLine?.addPoint(x, y)
    }

    fun clearCurrentLine() {
        _currentLine?.complete()
        _currentLine = null
    }

    fun undoLastAction(action: DrawingAction) {
        val currentLayer = layers.getOrNull(currentLayerIndex) ?: return

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
        val currentLayer = layers.getOrNull(currentLayerIndex) ?: return

        synchronized(currentLayer.lines) {
            when (action) {
                is DrawingAction.LineCompleted -> currentLayer.lines.add(action.line)
                else -> Unit
            }
        }
    }

    fun startEraser(x: Float, y: Float) {
        val currentLayer = layers.getOrNull(currentLayerIndex) ?: return
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

    private fun addLayer() {
        layers.add(Layer())
    }

    fun setActiveLayer(index: Int) {
        if (index in layers.indices) {
            currentLayerIndex = index
        }
    }

    fun normalizeCoordinate(
        x: Float, y: Float, left: Float, top: Float, width: Float, height: Float
    ): Pair<Float, Float> {
        val normX = (x - left) / width * 2f - 1f
        val normY = 1f - (y - top) / height * 2f
        return normX to normY
    }
}
