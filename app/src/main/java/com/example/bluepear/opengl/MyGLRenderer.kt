package com.example.bluepear.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.os.Handler
import android.os.Looper
import com.example.bluepear.ui.canvas.DrawingAction

class MyGLRenderer : GLSurfaceView.Renderer {
    private val lines = mutableListOf<Line>()
    private val linesToAdd = mutableListOf<Line>()
    private var _currentLine: Line? = null

    val currentLine: Line?
        get() = _currentLine

    var currentBrushColor: FloatArray = floatArrayOf(0f, 0f, 0f, 1f)
    var currentBrushSize: Float = 5f

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val mvpMatrix = FloatArray(16).apply {
            android.opengl.Matrix.setIdentityM(this, 0)
        }

        for (line in lines) {
            line.draw(mvpMatrix)
        }

        if (linesToAdd.isNotEmpty()) {
            lines.addAll(linesToAdd)
            linesToAdd.clear()
        }
    }

    fun startLine(x: Float, y: Float) {
        _currentLine = Line(currentBrushColor, currentBrushSize).also {
            it.addPoint(x, y)
            mainHandler.post {
                linesToAdd.add(it)
            }
        }
    }

    fun updateLine(x: Float, y: Float) {
        _currentLine?.addPoint(x, y)
    }

    fun clearCurrentLine() {
        _currentLine = null
    }



    fun undoLastAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.LineCompleted -> lines.remove(action.line)
            else -> Unit
        }
    }

    fun redoAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.LineCompleted -> lines.add(action.line)
            else -> Unit
        }
    }

    fun startEraser(x: Float, y: Float) {
        val newLines = mutableListOf<Line>()
        lines.forEach { line ->
            if (line.removeIntersectingPart(x, y, currentBrushSize)) {
                // Do not add the modified line if part was removed
            } else {
                newLines.add(line)
            }
        }

        lines.clear()
        lines.addAll(newLines)
    }

    fun normalizeCoordinate(
        x: Float, y: Float, left: Float, top: Float, width: Float, height: Float
    ): Pair<Float, Float> {
        val normX = (x - left) / width * 2f - 1f
        val normY = 1f - (y - top) / height * 2f
        return normX to normY
    }
}

