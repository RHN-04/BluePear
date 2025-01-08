package com.example.bluepear.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluepear.data.Work
import com.example.bluepear.ui.home.HomeScreen
import com.example.bluepear.ui.canvas.CanvasScreen
import com.example.bluepear.ui.thisproject.ProjectScreen
import com.example.bluepear.ui.newproject.NewProjectScreen

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()
    val works = remember { mutableStateListOf<Work>() }
    val workStorage = WorkStorage(context)

    works.addAll(workStorage.loadAllWorks())

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCreateNewWork = { navController.navigate("newwork") },
                works = works,
                onWorkSelected = { work ->
                    navController.navigate("work/${work.title}")
                }
            )
        }
        composable("newwork") {
            NewProjectScreen(
                onCanvasCreate = { title ->
                    val newWork = Work(title = title)
                    works.add(newWork)
                    workStorage.saveWork(newWork)
                    navController.navigate("canvas/${title}")
                }
            )
        }
        composable("canvas/{workTitle}") { backStackEntry ->
            val workTitle = backStackEntry.arguments?.getString("workTitle") ?: "Unknown"
            val work = workStorage.loadWork(workTitle) ?: Work(title = "Unknown")
            CanvasScreen(
                work = work,
                onBack = { navController.popBackStack() },
                onExport = { /* Handle export */ },
                onSave = { updatedWork ->
                    workStorage.saveWork(updatedWork)
                    val index = works.indexOfFirst { it.title == updatedWork.title }
                    if (index != -1) {
                        works[index] = updatedWork
                    }
                }
            )
        }

        composable("work/{workTitle}") { backStackEntry ->
            val workTitle = backStackEntry.arguments?.getString("workTitle") ?: "Unknown"
            val work = works.find { it.title == workTitle } ?: Work(title = "Unknown")
            ProjectScreen(
                work = work,
                onEdit = { navController.navigate("canvas/${work.title}") },
                onExport = { /* Handle export */ }
            )
        }
    }
}

