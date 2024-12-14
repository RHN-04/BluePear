package com.example.bluepear.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismissRequest: () -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    var brightness by remember { mutableStateOf(1f) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Выбор цвета") },
        text = {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                DrawColorRing(
                    modifier = Modifier.size(300.dp),
                    hue = hue,
                    onHueChange = { hue = it }
                )
                DrawColorTriangle(
                    modifier = Modifier.size(200.dp),
                    hue = hue,
                    onColorChanged = { newSaturation, newBrightness ->
                        saturation = newSaturation
                        brightness = newBrightness
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val color = Color.hsv(hue, saturation, brightness)
                onColorSelected(color)
            }) {
                Text("Выбрать")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun DrawColorRing(modifier: Modifier, hue: Float, onHueChange: (Float) -> Unit) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val center = size / 2
                val dx = offset.x - center.width
                val dy = offset.y - center.height
                val distance = hypot(dx, dy)
                val radius = min(size.width, size.height) / 2f

                if (distance in radius * 0.8f..radius) {
                    val angle = (atan2(dy, dx) * (180 / Math.PI).toFloat() + 360) % 360
                    onHueChange(angle)
                }
            }
        }
    ) {
        drawHueRing(size)
    }
}

@Composable
fun DrawColorTriangle(
    modifier: Modifier,
    hue: Float,
    onColorChanged: (Float, Float) -> Unit
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val center = size / 2
                val triangleHeight = size.height / 2f

                val localX = offset.x - center.width
                val localY = offset.y - (center.height - triangleHeight / 2)
                if (localY >= 0 && localY <= triangleHeight &&
                    localX >= -localY / 2 && localX <= localY / 2
                ) {
                    val saturation = max(0f, min(1f, (localX / triangleHeight) + 0.5f))
                    val brightness = max(0f, min(1f, 1f - (localY / triangleHeight)))
                    onColorChanged(saturation, brightness)
                }
            }
        }
    ) {
        drawSaturationBrightnessTriangle(hue, size)
    }
}

fun DrawScope.drawHueRing(size: Size) {
    val strokeWidth = size.minDimension / 10
    val radius = size.minDimension / 2 - strokeWidth / 2

    drawCircle(
        brush = Brush.sweepGradient(
            colors = (0..360 step 10).map {
                Color.hsv(it.toFloat(), 1f, 1f)
            }
        ),
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
}

fun DrawScope.drawSaturationBrightnessTriangle(hue: Float, size: Size) {
    val path = Path().apply {
        moveTo(center.x, center.y - size.height / 4)
        lineTo(center.x - size.width / 4, center.y + size.height / 4)
        lineTo(center.x + size.width / 4, center.y + size.height / 4)
        close()
    }
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.hsv(hue, 1f, 1f),
                Color.hsv(hue, 0f, 1f),
                Color.hsv(hue, 0f, 0f)
            )
        )
    )
}
