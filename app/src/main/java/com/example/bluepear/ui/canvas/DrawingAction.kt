package com.example.bluepear.ui.canvas

import com.example.bluepear.opengl.Line

sealed class DrawingAction {
    data class Start(val x: Float, val y: Float) : DrawingAction()
    data class Move(val x: Float, val y: Float) : DrawingAction()
    object End : DrawingAction()
    data class LineCompleted(val line: Line) : DrawingAction()
}
