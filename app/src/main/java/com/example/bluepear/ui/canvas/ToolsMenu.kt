package com.example.bluepear.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToolsMenu(
    currentColor: Color,
    onColorChange: (Color) -> Unit,
    isEraser: Boolean,
    brushSize: Float,
    onBrushToggle: () -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onLayerMenuOpen: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Slider with label for brush size
        Text(text = "Brush size: ${brushSize.toInt()}", modifier = Modifier.padding(top = 8.dp))
        BrushSizeSlider(
            brushSize = brushSize,
            onBrushSizeChange = onBrushSizeChange,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brush/Eraser toggle button
            Button(onClick = onBrushToggle) {
                Text(if (isEraser) "Eraser" else "Brush")
            }

            // Color picker button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(currentColor, CircleShape)
                    .clickable { showColorPicker = true }
            )

            // Layer menu button
            Button(
                onClick = onLayerMenuOpen
            ) {
                Text("Layer Menu")
            }
        }

        // Color picker dialog
        if (showColorPicker) {
            ColorPicker(
                initialColor = currentColor,
                onColorSelected = { color ->
                    onColorChange(color)
                    showColorPicker = false
                },
                onDismissRequest = { showColorPicker = false }
            )
        }
    }
}
