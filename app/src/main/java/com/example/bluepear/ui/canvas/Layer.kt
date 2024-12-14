package com.example.bluepear.ui.canvas

import com.example.bluepear.opengl.MyGLProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import android.util.Log

class Layer(private val program: MyGLProgram) {
    private val INITIAL_BUFFER_SIZE = 1024
    private var currentSize = 0

    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(INITIAL_BUFFER_SIZE * 4) // 4 bytes per float
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    fun addLine(x1: Float, y1: Float, x2: Float, y2: Float) {
        Log.d("Layer", "Adding line: ($x1, $y1) to ($x2, $y2)")
        val newPoints = floatArrayOf(x1, y1, x2, y2)
        updateBuffer(newPoints)
    }

    fun render(projectionMatrix: FloatArray) {
        Log.d("Layer", "Rendering layer. Buffer size: $currentSize")
        program.drawLine(vertexBuffer, currentSize, projectionMatrix)
    }

    private fun updateBuffer(newPoints: FloatArray) {
        if (currentSize + newPoints.size > vertexBuffer.capacity()) {
            // Expand buffer
            val newCapacity = (vertexBuffer.capacity() + newPoints.size) * 2
            val newBuffer = ByteBuffer
                .allocateDirect(newCapacity * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

            vertexBuffer.position(0)
            newBuffer.put(vertexBuffer)
            vertexBuffer.clear()
            vertexBuffer.put(newBuffer)
        }

        vertexBuffer.position(currentSize)
        vertexBuffer.put(newPoints)
        currentSize += newPoints.size
    }

}
