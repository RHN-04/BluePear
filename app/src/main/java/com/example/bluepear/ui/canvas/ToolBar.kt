package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToolBar(
    currentColor: Color,
    onSelectColor: (Color) -> Unit,
    onSelectBrushSize: (Float) -> Unit,
    currentBrushSize: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ColorPickerButton(currentColor = currentColor, onSelectColor = onSelectColor)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Brush Size: ${currentBrushSize.toInt()}")
            Slider(
                value = currentBrushSize,
                onValueChange = onSelectBrushSize,
                valueRange = 1f..50f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
