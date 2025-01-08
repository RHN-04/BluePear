package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bluepear.opengl.MyGLRenderer
import com.example.bluepear.opengl.MyGLProgram

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    val currentBrush = remember {
        mutableStateOf(BluePearBrush(color = Color.Black, size = 5f, type = BrushType.NORMAL))
    }
    val isEraser = remember { mutableStateOf(false) }
    val glRenderer = remember { MyGLRenderer() }
    val actions = remember { mutableStateListOf<DrawingAction>() }
    val undoneActions = remember { mutableStateListOf<DrawingAction>() }
    val layers = remember { mutableStateListOf(Layer(id = 1, program = MyGLProgram())) }
    var activeLayerId by remember { mutableStateOf(layers.first().id) }

    var isLayersMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canvas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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
                onUndo = {
                    if (actions.isNotEmpty()) {
                        val lastAction = actions.removeAt(actions.lastIndex)
                        undoneActions.add(lastAction)
                        glRenderer.undoLastAction(lastAction)
                    }
                },
                onRedo = {
                    if (undoneActions.isNotEmpty()) {
                        val redoAction = undoneActions.removeAt(undoneActions.lastIndex)
                        actions.add(redoAction)
                        glRenderer.redoAction(redoAction)
                    }
                }
            )

            DrawingCanvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                brush = currentBrush.value,
                glRenderer = glRenderer,
                activeLayerId = activeLayerId,
                onAction = { action ->
                    when (action) {
                        is DrawingAction.Start -> actions.add(action)
                        is DrawingAction.End -> {
                            val completedLine = glRenderer.currentLine
                            if (completedLine != null) {
                                actions.add(DrawingAction.LineCompleted(completedLine))
                                glRenderer.clearCurrentLine()
                                undoneActions.clear()
                            }
                        }
                        else -> {}
                    }
                }
            )

            ToolsMenu(
                currentColor = currentBrush.value.color,
                isEraser = isEraser.value,
                brushSize = currentBrush.value.size,
                onBrushToggle = {
                    isEraser.value = !isEraser.value
                    currentBrush.value = currentBrush.value.copy(
                        type = if (isEraser.value) BrushType.ERASER else BrushType.NORMAL
                    )
                },
                onBrushSizeChange = { newSize ->
                    currentBrush.value = currentBrush.value.copy(size = newSize)
                },
                onColorChange = { newColor ->
                    currentBrush.value = currentBrush.value.copy(color = newColor)
                },
                onLayerMenuOpen = { isLayersMenuOpen = true }
            )
        }
    }

    if (isLayersMenuOpen) {
        LayersMenu(
            layers = layers,
            activeLayerId = activeLayerId,
            onSetActiveLayer = { id ->
                activeLayerId = id
                glRenderer.setActiveLayer(id)
            },

            onLayerRemove = { id ->
                if (layers.size > 1) {
                    layers.removeIf { it.id == id }
                    glRenderer.removeLayer(id)
                    activeLayerId = layers.lastOrNull()?.id ?: 1
                    glRenderer.setActiveLayer(activeLayerId)
                }
            },
            onAddLayer = {
                val newLayerId = glRenderer.addLayer()
                layers.add(Layer(id = newLayerId, program = MyGLProgram()))
            },
            onClose = { isLayersMenuOpen = false },
            glRenderer = glRenderer
        )
    }
}
