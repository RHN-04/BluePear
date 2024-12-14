package com.example.bluepear.ui.canvas

import com.example.bluepear.opengl.MyGLProgram

class LayersManager {
    private val layers = mutableListOf<Layer>()
    private lateinit var program: MyGLProgram

    fun init() {
        program = MyGLProgram()
        program.init()
        addLayer()
    }

    fun addLayer() {
        layers.add(Layer(program))
    }

    fun getTopLayer(): Layer? {
        return layers.lastOrNull()
    }

    fun render(mvpMatrix: FloatArray) {
        layers.forEach { it.render(mvpMatrix) }
    }
}
