package com.example.bluepear.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCreateNewWork: () -> Unit,
    works: List<String>, // Список работ
    onWorkSelected: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNewWork) {
                Icon(Icons.Default.Add, contentDescription = "Create New Work")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(works) { work ->
                Text(
                    text = work,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWorkSelected(work) }
                        .padding(16.dp)
                )
            }
        }
    }
}
