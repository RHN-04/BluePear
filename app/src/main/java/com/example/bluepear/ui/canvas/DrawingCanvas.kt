package com.example.bluepear.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingCanvas(
    paths: MutableList<Pair<Path, Pair<Color, Float>>>,
    currentPath: MutableState<Path?>,
    currentColor: MutableState<Color>,
    brushSize: MutableState<Float>
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath.value = Path().apply { moveTo(offset.x, offset.y) }
                    },
                    onDrag = { change, _ ->
                        currentPath.value?.lineTo(change.position.x, change.position.y)
                    },
                    onDragEnd = {
                        currentPath.value?.let { path ->
                            paths.add(path to (currentColor.value to brushSize.value))
                            currentPath.value = null
                        }
                    }
                )
            }
    ) {
        paths.forEach { (path, params) ->
            val (pathColor, pathBrushSize) = params
            drawPath(path, color = pathColor, style = Stroke(pathBrushSize))
        }

        currentPath.value?.let {
            drawPath(it, color = currentColor.value, style = Stroke(brushSize.value))
        }
    }
}


