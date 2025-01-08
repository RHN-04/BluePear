package com.example.bluepear.ui.canvas

import com.example.bluepear.opengl.Line
import com.example.bluepear.opengl.MyGLProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.CopyOnWriteArrayList

data class Layer(
    val id: Int,
    private val program: MyGLProgram = MyGLProgram(),
    var isVisible: Boolean = true,
    var opacity: Float = 1.0f
) {
    private val INITIAL_BUFFER_SIZE = 1024
    private var currentSize = 0

    private var vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(INITIAL_BUFFER_SIZE * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    val lines = mutableListOf<Line>()
    val linesToAdd = CopyOnWriteArrayList<Line>()

    fun toggleVisibility() {
        isVisible = !isVisible
    }

    fun addVertex(x: Float, y: Float) {
        if (vertexBuffer.remaining() < 2) {
            expandBuffer()
        }
        vertexBuffer.put(x)
        vertexBuffer.put(y)
        currentSize += 2
    }

    fun draw(projectionMatrix: FloatArray) {
        if (isVisible) {
            synchronized(lines) {
                for (line in lines) {
                    line.draw(projectionMatrix)
                }
            }
            synchronized(linesToAdd) {
                for (line in linesToAdd) {
                    line.draw(projectionMatrix)
                }
            }
        }
    }

    fun clear() {
        vertexBuffer.clear()
        currentSize = 0
        lines.clear()
        linesToAdd.clear()
    }

    private fun expandBuffer() {
        val newBuffer = ByteBuffer
            .allocateDirect(vertexBuffer.capacity() * 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.rewind()
        newBuffer.put(vertexBuffer)
        vertexBuffer = newBuffer
    }
}
