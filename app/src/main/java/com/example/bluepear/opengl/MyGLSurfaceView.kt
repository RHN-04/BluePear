package com.example.bluepear.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private var glRenderer: MyGLRenderer? = null

    init {
        setEGLContextClientVersion(2)
    }

    fun setMyRenderer(renderer: MyGLRenderer) {
        if (glRenderer == null) {
            glRenderer = renderer
            setRenderer(renderer)
            renderMode = RENDERMODE_WHEN_DIRTY
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }
}
