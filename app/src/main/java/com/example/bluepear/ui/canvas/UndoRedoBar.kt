package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UndoRedoBar(
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        IconButton(onClick = onUndo) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo")
        }
        IconButton(onClick = onRedo) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Redo")
        }
    }
}

