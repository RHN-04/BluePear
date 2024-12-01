package com.example.bluepear.ui.canvas

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UndoRedoBar(
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onUndo) {
            Text("Undo")
        }
        Button(onClick = onRedo) {
            Text("Redo")
        }
    }
}
