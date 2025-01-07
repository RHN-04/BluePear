package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LayersMenu(
    layers: List<Layer>,
    activeLayerIndex: Int,
    onSetActiveLayer: (Int) -> Unit,
    onLayerToggle: (Int) -> Unit,
    onOpacityChange: (Int, Float) -> Unit,
    onLayerRemove: (Int) -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) { Text("Закрыть") }
        },
        title = { Text("Слои") },
        text = {
            Column {
                layers.forEachIndexed { index, layer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Слой ${index + 1}", color = if (index == activeLayerIndex) Color.Blue else Color.Black)
                        Checkbox(
                            checked = layer.isVisible,
                            onCheckedChange = { onLayerToggle(index) }
                        )
                        Slider(
                            value = layer.opacity,
                            onValueChange = { newOpacity -> onOpacityChange(index, newOpacity) },
                            valueRange = 0f..1f
                        )
                        IconButton(onClick = { onLayerRemove(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }
        }
    )
}

