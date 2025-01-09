package com.example.bluepear.ui.canvas

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

fun Color.toHsv(): FloatArray {
    val r = red
    val g = green
    val b = blue

    val androidColor = AndroidColor.rgb(
        (r * 255).toInt(),
        (g * 255).toInt(),
        (b * 255).toInt()
    )

    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(androidColor, hsv)
    return hsv
}

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismissRequest: () -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    var brightness by remember { mutableStateOf(1f) }

    LaunchedEffect(initialColor) {
        val hsv = initialColor.toHsv()
        hue = hsv[0]
        saturation = hsv[1]
        brightness = hsv[2]
    }

    val selectedColor = Color.hsv(hue, saturation, brightness)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Выбор цвета") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        modifier = Modifier.size(240.dp),
                        hue = hue,
                        saturation = saturation,
                        brightness = brightness,
                        onColorChanged = { newSaturation, newBrightness ->
                            saturation = newSaturation
                            brightness = newBrightness
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColorPreview("Текущий цвет", initialColor)
                    ColorPreview("Выбранный цвет", selectedColor)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(selectedColor) }) {
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
fun ColorPreview(label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}


@Composable
fun DrawColorRing(modifier: Modifier, hue: Float, onHueChange: (Float) -> Unit) {
    var lastPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    val distanceThreshold = 10f
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                val dx = change.position.x - center.x
                val dy = change.position.y - center.y
                val distance = hypot(dx, dy)

                if (distance > distanceThreshold) {
                    val angle = (atan2(dy, dx) * (180 / Math.PI).toFloat() + 360) % 360
                    onHueChange(angle)
                    lastPosition = change.position
                }
            }
        }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        drawHueRing(size)

        val radius = min(size.width, size.height) / 2f
        val angleRadians = Math.toRadians(hue.toDouble())
        val markerX = center.x + cos(angleRadians).toFloat() * radius * 0.9f
        val markerY = center.y + sin(angleRadians).toFloat() * radius * 0.9f

        drawCircle(Color.Black, radius = 10f, center = Offset(markerX, markerY), style = Stroke(width = 2f))
    }
}

@Composable
fun DrawColorTriangle(
    modifier: Modifier,
    hue: Float,
    saturation: Float,
    brightness: Float,
    onColorChanged: (Float, Float) -> Unit
) {
    val cachedBitmap = remember(hue) {
        createSaturationBrightnessBitmap(hue, 240)
    }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val sizePx = size.width
                val radius = sizePx * 0.56f
                val triangleRadius = radius * 1f
                val center = Offset(sizePx / 2f, sizePx / 2f)

                val angleLeft = Math.toRadians(150.0)
                val angleRight = Math.toRadians(30.0)
                val angleTop = Math.toRadians(270.0)

                val leftVertex = Offset(
                    center.x + triangleRadius * cos(angleLeft).toFloat(),
                    center.y + triangleRadius * sin(angleLeft).toFloat()
                )
                val rightVertex = Offset(
                    center.x + triangleRadius * cos(angleRight).toFloat(),
                    center.y + triangleRadius * sin(angleRight).toFloat()
                )
                val topVertex = Offset(
                    center.x + triangleRadius * cos(angleTop).toFloat(),
                    center.y + triangleRadius * sin(angleTop).toFloat()
                )

                val bary = barycentricCoords(change.position, topVertex, leftVertex, rightVertex)

                if (bary.first >= 0f && bary.second >= 0f && bary.third >= 0f) {
                    onColorChanged(
                        bary.second.coerceIn(0f, 1f),
                        (1f - bary.first).coerceIn(0f, 1f)
                    )
                }
            }
        }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.56f
        val imageSize = radius * 2

        drawIntoCanvas { canvas ->
            val scale = imageSize / cachedBitmap.width
            canvas.save()
            canvas.translate(center.x - radius, center.y - radius)
            canvas.scale(scale, scale)
            canvas.nativeCanvas.drawBitmap(cachedBitmap, 0f, 0f, null)
            canvas.restore()
        }

        val triangleRadius = radius * 0.7f
        val angleLeft = Math.toRadians(150.0)
        val angleRight = Math.toRadians(30.0)
        val angleTop = Math.toRadians(270.0)

        val leftVertex = Offset(
            center.x + triangleRadius * cos(angleLeft).toFloat(),
            center.y + triangleRadius * sin(angleLeft).toFloat()
        )
        val rightVertex = Offset(
            center.x + triangleRadius * cos(angleRight).toFloat(),
            center.y + triangleRadius * sin(angleRight).toFloat()
        )
        val topVertex = Offset(
            center.x + triangleRadius * cos(angleTop).toFloat(),
            center.y + triangleRadius * sin(angleTop).toFloat()
        )

        val markerPosition = calculateMarkerPosition(
            saturation = saturation.coerceIn(0f, 1f),
            brightness = (1f - brightness).coerceIn(0f, 1f),
            topVertex = topVertex,
            leftVertex = leftVertex,
            rightVertex = rightVertex
        )

        drawCircle(
            Color.Black,
            radius = 8f,
            center = markerPosition,
            style = Stroke(width = 2f)
        )
    }
}

fun calculateMarkerPosition(
    saturation: Float,
    brightness: Float,
    topVertex: Offset,
    leftVertex: Offset,
    rightVertex: Offset
): Offset {
    val adjustedSaturation = saturation * 0.95f
    val adjustedBrightness = (1f - brightness) * 0.95f

    val x = topVertex.x * (1 - adjustedBrightness) +
            rightVertex.x * (adjustedBrightness * (1 - adjustedSaturation)) +
            leftVertex.x * (adjustedBrightness * adjustedSaturation)

    val y = topVertex.y * (1 - adjustedBrightness) +
            rightVertex.y * (adjustedBrightness * (1 - adjustedSaturation)) +
            leftVertex.y * (adjustedBrightness * adjustedSaturation)

    return Offset(x, y)
}


fun createSaturationBrightnessBitmap(hue: Float, sizePx: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val center = Offset(sizePx / 2f, sizePx / 2f)
    val radius = sizePx / 2f * 0.8f

    val topVertex = Offset(center.x, center.y - radius)
    val leftVertex = Offset(center.x - radius * 0.866f, center.y + radius / 2f)
    val rightVertex = Offset(center.x + radius * 0.866f, center.y + radius / 2f)

    for (y in 0 until sizePx) {
        for (x in 0 until sizePx) {
            val point = Offset(x.toFloat(), y.toFloat())
            val bary = barycentricCoords(point, topVertex, leftVertex, rightVertex)

            if (bary.first >= 0 && bary.second >= 0 && bary.third >= 0) {
                val color = interpolateColor(
                    bary,
                    Color.Black,
                    Color.hsv(hue, 1f, 1f),
                    Color.White
                )
                bitmap.setPixel(x, y, color.toArgb())
            } else {
                bitmap.setPixel(x, y, Color.Transparent.toArgb())
            }
        }
    }
    return bitmap
}

fun interpolateColor(
    bary: Triple<Float, Float, Float>,
    color1: Color,
    color2: Color,
    color3: Color
): Color {
    val (lambda1, lambda2, lambda3) = bary
    val r = lambda1 * color1.red + lambda2 * color2.red + lambda3 * color3.red
    val g = lambda1 * color1.green + lambda2 * color2.green + lambda3 * color3.green
    val b = lambda1 * color1.blue + lambda2 * color2.blue + lambda3 * color3.blue
    return Color(r, g, b)
}

fun barycentricCoords(p: Offset, a: Offset, b: Offset, c: Offset): Triple<Float, Float, Float> {
    val detT = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y)
    val lambda1 = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / detT
    val lambda2 = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / detT
    val lambda3 = 1f - lambda1 - lambda2
    return Triple(lambda1, lambda2, lambda3)
}

fun DrawScope.drawHueRing(size: Size) {
    val radius = min(size.width, size.height) / 2f
    val innerRadius = radius * 0.8f
    val strokeWidth = radius - innerRadius

    val shader = Brush.sweepGradient(
        colors = List(361) { angle ->
            Color.hsv(angle.toFloat(), 1f, 1f)
        }
    )

    drawCircle(
        brush = shader,
        radius = radius - strokeWidth / 2,
        center = Offset(size.width / 2, size.height / 2),
        style = Stroke(width = strokeWidth)
    )
}