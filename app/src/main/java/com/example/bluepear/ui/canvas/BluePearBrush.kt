package com.example.bluepear.ui.canvas

import androidx.compose.ui.graphics.Color

data class BluePearBrush(
    val color: Color = Color.Black,
    val size: Float = 5f,
    val type: BrushType = BrushType.NORMAL
) {
    fun colorAsFloatArray(): FloatArray {
        return when (type) {
            BrushType.NORMAL -> floatArrayOf(color.red, color.green, color.blue, color.alpha)
            BrushType.ERASER -> floatArrayOf(1f, 1f, 1f, 1f)
        }
    }
}

enum class BrushType {
    NORMAL,
    ERASER
}
