package com.example.bluepear.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerButton(
    currentColor: Color,
    onSelectColor: (Color) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Black)

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(currentColor, shape = CircleShape)
            .clickable { isDialogOpen = true }
    )

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                Button(onClick = { isDialogOpen = false }) {
                    Text("Close")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Color")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, shape = CircleShape)
                                    .clickable {
                                        onSelectColor(color)
                                        isDialogOpen = false
                                    }
                            )
                        }
                    }
                }
            }
        )
    }
}
