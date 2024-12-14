package com.example.bluepear.ui.canvas

sealed class DrawingAction {
    data class Start(val x: Float, val y: Float) : DrawingAction()
    data class Move(val x: Float, val y: Float) : DrawingAction()
    object End : DrawingAction()
}
