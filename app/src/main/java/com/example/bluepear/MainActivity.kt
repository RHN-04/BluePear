package com.example.bluepear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bluepear.opengl.MyGLRenderer
import com.example.bluepear.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glRenderer = MyGLRenderer()

        setContent {
            AppNavigation(context = this, glRenderer = glRenderer)
        }
    }
}

