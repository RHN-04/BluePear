package com.example.bluepear.ui.canvas

import ScalableCanvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    val currentColor = remember { mutableStateOf(Color.Black) }
    val brushSize = remember { mutableStateOf(5f) }
    val paths = remember { mutableStateListOf<Pair<Path, Pair<Color, Float>>>() }
    val currentPath = remember { mutableStateOf<Path?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canvas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                actions = {
                    IconButton(onClick = onExport) {
                        Text("Export")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            UndoRedoBar(
                onUndo = { if (paths.isNotEmpty()) paths.removeLast() },
                onRedo = { /* Логика повтора */ }
            )

            Box(modifier = Modifier.weight(1f)) {
                ScalableCanvas {
                    DrawingCanvas(
                        paths = paths,
                        currentPath = currentPath,
                        currentColor = currentColor,
                        brushSize = brushSize
                    )
                }
            }

            ToolBar(
                currentColor = currentColor.value,
                onSelectColor = { newColor ->
                    currentColor.value = newColor
                },
                onSelectBrushSize = { newSize ->
                    brushSize.value = newSize
                },
                currentBrushSize = brushSize.value
            )
        }
    }
}
