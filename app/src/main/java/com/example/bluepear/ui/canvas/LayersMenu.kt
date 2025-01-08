package com.example.bluepear.ui.canvas

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.bluepear.opengl.MyGLRenderer

@Composable
fun LayersMenu(
    layers: List<Layer>,
    activeLayerId: Int,
    onSetActiveLayer: (Int) -> Unit,
    onLayerRemove: (Int) -> Unit,
    onAddLayer: () -> Unit,
    onClose: () -> Unit,
    glRenderer: MyGLRenderer
) {
    val layerVisibilityState = remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) { Text("Закрыть") }
        },
        title = { Text("Слои") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                Button(
                    onClick = {
                        onAddLayer()
                        Log.d("UI", "Layer added")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("+")
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(layers.sortedByDescending { it.id }) { layer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Слой ${layer.id}",
                                color = if (layer.id == activeLayerId) Color.Blue else Color.Black,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .clickable {
                                        onSetActiveLayer(layer.id)
                                        Log.d("UI", "Active layer set to ${layer.id}")
                                    }
                            )

                            val isLayerVisible = layerVisibilityState.value[layer.id] ?: glRenderer.getLayerVisibility(layer.id)

                            Checkbox(
                                checked = isLayerVisible,
                                onCheckedChange = { isChecked ->
                                    glRenderer.toggleLayerVisibility(layer.id, isChecked)
                                    layerVisibilityState.value = layerVisibilityState.value.toMutableMap().apply {
                                        put(layer.id, isChecked)
                                    }
                                    Log.d("UI", "Layer ${layer.id} visibility toggled to $isChecked")
                                }
                            )

                            IconButton(onClick = {
                                onLayerRemove(layer.id)
                                Log.d("UI", "Layer ${layer.id} removed")
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Удалить слой"
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
