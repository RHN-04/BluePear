package com.example.bluepear.ui.canvas

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bluepear.opengl.MyGLRenderer
import com.example.bluepear.opengl.MyGLSurfaceView

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    brush: BluePearBrush,
    glRenderer: MyGLRenderer,
    onAction: (DrawingAction) -> Unit
) {
    AndroidView(
        factory = { context ->
            MyGLSurfaceView(context).apply {
                setMyRenderer(glRenderer)

                setOnTouchListener { _, event ->
                    // Получаем координаты области рисования на экране
                    val location = IntArray(2)
                    getLocationOnScreen(location)
                    val canvasLeft = 0f
                    val canvasTop = 0f

                    // Получаем реальные размеры холста
                    val canvasWidth = width.toFloat()
                    val canvasHeight = height.toFloat()

                    // Нормализуем координаты касания с учетом размеров холста
                    val (normX, normY) = glRenderer.normalizeCoordinate(
                        event.x, event.y, canvasLeft, canvasTop, canvasWidth, canvasHeight
                    )

                    // Логируем координаты касания
                    Log.d(
                        "DrawingCanvas",
                        "Action: ${event.action}, Raw X: ${event.x}, Raw Y: ${event.y}, Norm X: $normX, Norm Y: $normY"
                    )

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            glRenderer.startLine(normX, normY, brush.colorAsFloatArray(), brush.size)
                            onAction(DrawingAction.Start(normX, normY))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            glRenderer.updateLine(normX, normY)
                            onAction(DrawingAction.Move(normX, normY))
                        }
                        MotionEvent.ACTION_UP -> {
                            glRenderer.finishLine()
                            onAction(DrawingAction.End)
                        }
                    }
                    this.requestRender()
                    true
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
