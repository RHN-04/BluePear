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
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        IconButton(onClick = onUndo, modifier = Modifier.padding(end = 8.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Отменить")
        }
        IconButton(onClick = onRedo) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Повторить")
        }
    }
}

