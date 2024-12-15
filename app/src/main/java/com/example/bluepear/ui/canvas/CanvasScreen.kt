package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bluepear.opengl.MyGLRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    val currentBluePearBrush = remember {
        mutableStateOf(BluePearBrush(color = Color.Black, size = 5f, type = BrushType.NORMAL))
    }
    val isEraser = remember { mutableStateOf(false) }
    val glRenderer = remember { MyGLRenderer() }

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
            // Канвас для рисования
            DrawingCanvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                brush = currentBluePearBrush.value,
                glRenderer = glRenderer,
                onAction = { action ->

                }
            )

            ToolsMenu(
                currentColor = currentBluePearBrush.value.color,
                isEraser = isEraser.value,
                brushSize = currentBluePearBrush.value.size,
                onBrushToggle = {
                    isEraser.value = !isEraser.value
                    currentBluePearBrush.value = currentBluePearBrush.value.copy(
                        type = if (isEraser.value) BrushType.ERASER else BrushType.NORMAL
                    )
                },
                onBrushSizeChange = { newSize ->
                    currentBluePearBrush.value = currentBluePearBrush.value.copy(size = newSize)
                },
                onColorChange = { newColor ->
                    currentBluePearBrush.value = currentBluePearBrush.value.copy(color = newColor)
                },
                onLayerMenuOpen = {
                    // Открытие меню слоёв
                }
            )
        }
    }
}
