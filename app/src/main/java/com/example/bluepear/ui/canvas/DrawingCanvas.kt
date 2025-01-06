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
    onAction: (DrawingAction) -> Unit
) {
    LaunchedEffect(brush) {
        glRenderer.currentBrushColor = brush.colorAsFloatArray()
        glRenderer.currentBrushSize = brush.size
    }

    AndroidView(
        factory = { context ->
            MyGLSurfaceView(context).apply {
                setMyRenderer(glRenderer)

                setOnTouchListener { _, event ->
                    val location = IntArray(2)
                    getLocationOnScreen(location)
                    val canvasLeft = 0f
                    val canvasTop = 0f
                    val canvasWidth = width.toFloat()
                    val canvasHeight = height.toFloat()
                    val (normX, normY) = glRenderer.normalizeCoordinate(
                        event.x, event.y, canvasLeft, canvasTop, canvasWidth, canvasHeight
                    )

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (brush.type == BrushType.ERASER) {
                                glRenderer.startEraser(normX, normY)
                            } else {
                                glRenderer.startLine(normX, normY)
                            }
                            onAction(DrawingAction.Start(normX, normY))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (brush.type == BrushType.ERASER) {
                                glRenderer.startEraser(normX, normY)
                            } else {
                                glRenderer.updateLine(normX, normY)
                            }
                            onAction(DrawingAction.Move(normX, normY))
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

