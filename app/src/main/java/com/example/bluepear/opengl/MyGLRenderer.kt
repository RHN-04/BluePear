package com.example.bluepear.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val lines = mutableListOf<Line>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f) // Белый фон
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        lines.forEach { it.draw(mvpMatrix) }
    }

    fun startLine(x: Float, y: Float, color: FloatArray, width: Float) {
        val line = Line(color, width)
        line.addPoint(x, y)
        lines.add(line)
    }

    fun updateLine(x: Float, y: Float) {
        lines.lastOrNull()?.addPoint(x, y)
    }

    fun finishLine() {
        // Дополнительная логика завершения линии
    }

    fun normalizeCoordinate(
        x: Float,
        y: Float,
        canvasLeft: Float,
        canvasTop: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ): Pair<Float, Float> {
        // Преобразование координат x
        val normX = 2 * ((x - canvasLeft) / canvasWidth) - 1

        // Преобразование координат y
        val normY = 1 - 2 * ((y - canvasTop) / canvasHeight)

        return Pair(normX, normY)
    }

}
