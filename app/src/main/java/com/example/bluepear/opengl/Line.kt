package com.example.bluepear.opengl

import android.opengl.GLES20
import java.nio.FloatBuffer

class Line(private val color: FloatArray, private val width: Float) {
    private val points = mutableListOf<Float>()

    fun addPoint(x: Float, y: Float) {
        points.add(x)
        points.add(y)
    }

    fun draw(mvpMatrix: FloatArray) {
        if (points.size < 4 || points.size % 2 != 0) return

        val pointsToDraw = points.toFloatArray()

        val vertexBuffer: FloatBuffer = BufferUtil.createFloatBuffer(pointsToDraw)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glLineWidth(width)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, pointsToDraw.size / 2)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    fun removeIntersectingPart(x: Float, y: Float, radius: Float): Boolean {
        val newPoints = mutableListOf<Float>()
        var partRemoved = false

        for (i in 0 until points.size step 2) {
            val x1 = points[i]
            val y1 = points[i + 1]
            val x2 = if (i + 2 < points.size) points[i + 2] else x1
            val y2 = if (i + 3 < points.size) points[i + 3] else y1

            if (isIntersectingWithCircle(x1, y1, x2, y2, x, y, radius)) {
                partRemoved = true
            } else {
                newPoints.add(x1)
                newPoints.add(y1)
            }
        }

        if (partRemoved) {
            points.clear()
            points.addAll(newPoints)
        }

        return partRemoved
    }


    private fun isIntersectingWithCircle(x1: Float, y1: Float, x2: Float, y2: Float, cx: Float, cy: Float, r: Float): Boolean {
        val dx = x2 - x1
        val dy = y2 - y1
        val fx = x1 - cx
        val fy = y1 - cy

        val a = dx * dx + dy * dy
        val b = 2 * (fx * dx + fy * dy)
        val c = (fx * fx + fy * fy) - r * r

        val discriminant = b * b - 4 * a * c
        return discriminant > 0
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """
    }
}
