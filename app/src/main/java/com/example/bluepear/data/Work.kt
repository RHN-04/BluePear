package com.example.bluepear.data

import com.example.bluepear.opengl.Line
import com.example.bluepear.ui.canvas.Layer

data class Work(
    val title: String,
    val layers: MutableList<Layer> = mutableListOf(),
    val lines: MutableList<Line> = mutableListOf()
)
