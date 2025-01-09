package com.example.bluepear.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluepear.data.Work
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*

@Composable
fun HomeScreen(
    onCreateNewWork: () -> Unit,
    works: List<Work>,
    onWorkSelected: (Work) -> Unit,
    onWorkDeleted: (Work) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onCreateNewWork, modifier = Modifier.fillMaxWidth()) {
            Text("Новый проект...")
        }

        LazyColumn {
            items(works) { work ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = work.title,
                        modifier = Modifier
                            .clickable { onWorkSelected(work) }
                    )
                    IconButton(
                        onClick = { onWorkDeleted(work) }
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }
    }
}


