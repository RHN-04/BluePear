package com.example.bluepear.ui.canvas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bluepear.data.Work
import com.example.bluepear.opengl.MyGLRenderer
import com.example.bluepear.opengl.MyGLProgram
import kotlinx.coroutines.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    work: Work,
    onBack: () -> Unit,
    onExport: () -> Unit,
    onSave: (Work) -> Unit
) {
    val currentBrush = remember {
        mutableStateOf(BluePearBrush(color = Color.Black, size = 5f, type = BrushType.NORMAL))
    }
    val isEraser = remember { mutableStateOf(false) }
    val glRenderer = remember { MyGLRenderer() }
    val actions = remember { mutableStateListOf<DrawingAction>() }
    val undoneActions = remember { mutableStateListOf<DrawingAction>() }
    val layers = remember {
        if (work.layers.isEmpty()) {
            mutableStateListOf(Layer(id = 1, program = MyGLProgram()))
        } else {
            mutableStateListOf(*work.layers.toTypedArray())
        }
    }

    var activeLayerId by remember { mutableStateOf(layers.first().id) }
    var isLayersMenuOpen by remember { mutableStateOf(false) }
    var isSavingInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(layers) {
        glRenderer.setLayers(layers)
    }

    LaunchedEffect(work, layers, actions) {
        while (isActive) {
            delay(5000)
            if (!isSavingInProgress) {
                isSavingInProgress = true
                onSave(work.copy(layers = layers, lines = actions.filterIsInstance<DrawingAction.LineCompleted>().map { it.line }.toMutableList()))
                isSavingInProgress = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(work.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onExport) {
                        Icon(Icons.Filled.Share, contentDescription = "Экспортировать")
                    }

                    IconButton(onClick = {
                        isSavingInProgress = true
                        onSave(work.copy(layers = layers, lines = actions.filterIsInstance<DrawingAction.LineCompleted>().map { it.line }.toMutableList()))
                        isSavingInProgress = false
                    }) {
                        Text("Сохранить")
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
                onBrushSizeChange = { newSize -> currentBrush.value = currentBrush.value.copy(size = newSize) },
                onColorChange = { newColor -> currentBrush.value = currentBrush.value.copy(color = newColor) },
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
