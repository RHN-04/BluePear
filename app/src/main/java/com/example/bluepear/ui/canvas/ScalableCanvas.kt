package com.example.bluepear.ui.canvas

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ScalableCanvas(
    modifier: Modifier = Modifier,
    onScaleChange: (Float) -> Unit,
    onPanChange: (Float, Float) -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onScaleChange(zoom)
                    onPanChange(pan.x, pan.y)
                }
            }
    ) {
        content()
    }
}
