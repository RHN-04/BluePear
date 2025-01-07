package com.example.bluepear.opengl

import android.opengl.GLES20

fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
        val error = GLES20.glGetShaderInfoLog(shader)
        println("Shader compilation error: $error")
        GLES20.glDeleteShader(shader)
        throw RuntimeException("Error compiling shader: $error")
    }
    return shader
}

