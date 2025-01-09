package com.example.bluepear.ui.canvas

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BrushSizeSlider(
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = brushSize,
        onValueChange = onBrushSizeChange,
        valueRange = 1f..50f,
        modifier = modifier
    )
}
