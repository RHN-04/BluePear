package com.example.bluepear.ui.canvas

import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bluepear.opengl.MyGLRenderer
import com.example.bluepear.opengl.MyGLSurfaceView

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    brush: BluePearBrush,
    glRenderer: MyGLRenderer,
    activeLayerId: Int,
    onAction: (DrawingAction) -> Unit
) {
    LaunchedEffect(brush, activeLayerId) {
        glRenderer.currentBrushColor = brush.colorAsFloatArray()
        glRenderer.currentBrushSize = brush.size
        glRenderer.setActiveLayer(activeLayerId)
    }

    AndroidView(
        factory = { context ->
            MyGLSurfaceView(context).apply {
                setMyRenderer(glRenderer)

                setOnTouchListener { _, event ->
                    val location = IntArray(2)
                    getLocationInWindow(location)

                    val canvasLeft = location[0].toFloat()
                    val canvasTop = location[1].toFloat()

                    val canvasWidth = width.toFloat()
                    val canvasHeight = height.toFloat()

                    val normalizedCoordinates = glRenderer.normalizeCoordinate(
                        event.x, event.y, canvasLeft, canvasTop, canvasWidth, canvasHeight
                    )
                    val normalizedX = normalizedCoordinates.first
                    val normalizedY = normalizedCoordinates.second

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (brush.type == BrushType.ERASER) {
                                glRenderer.startEraser(normalizedX, normalizedY)
                            } else {
                                glRenderer.startLine(normalizedX, normalizedY)
                            }
                            onAction(DrawingAction.Start(normalizedX, normalizedY))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (brush.type == BrushType.ERASER) {
                                glRenderer.startEraser(normalizedX, normalizedY)
                            } else {
                                glRenderer.updateLine(normalizedX, normalizedY)
                            }
                            onAction(DrawingAction.Move(normalizedX, normalizedY))
                        }
                        MotionEvent.ACTION_UP -> {
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
