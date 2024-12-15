package com.example.bluepear.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private val lines = mutableListOf<Line>()
    private var currentLine: Line? = null

    // Добавлены свойства для хранения текущего цвета и размера кисти
    var currentBrushColor: FloatArray = floatArrayOf(0f, 0f, 0f, 1f) // Черный по умолчанию
    var currentBrushSize: Float = 5f // Размер по умолчанию

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
    }

    fun startLine(x: Float, y: Float) {
        Log.d("MyGLRenderer", "Start line with color: ${currentBrushColor.contentToString()}, size: $currentBrushSize")
        currentLine = Line(currentBrushColor, currentBrushSize).also {
            it.addPoint(x, y)
            lines.add(it)
        }
    }


    fun updateLine(x: Float, y: Float) {
        currentLine?.addPoint(x, y)
    }

    fun normalizeCoordinate(
        x: Float, y: Float, left: Float, top: Float, width: Float, height: Float
    ): Pair<Float, Float> {
        val normX = (x - left) / width * 2f - 1f
        val normY = 1f - (y - top) / height * 2f
        return normX to normY
    }
}
