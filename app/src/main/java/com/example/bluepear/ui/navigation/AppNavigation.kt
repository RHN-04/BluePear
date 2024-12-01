package com.example.bluepear.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluepear.ui.home.HomeScreen
import com.example.bluepear.ui.canvas.CanvasScreen
import com.example.bluepear.ui.thisproject.ProjectScreen
import com.example.bluepear.ui.newproject.NewProjectScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCreateNewWork = { navController.navigate("newwork") },
                works = listOf("Work 1", "Work 2"),
                onWorkSelected = { work -> navController.navigate("work/$work") }
            )
        }
        composable("newwork") {
            NewProjectScreen(
                onCanvasCreate = { width, height ->
                    navController.navigate("canvas")
                }
            )
        }
        composable("canvas") {
            CanvasScreen(
                onBack = { navController.popBackStack() },
                onExport = { /* Handle export */ }
            )
        }
        composable("work/{workName}") { backStackEntry ->
            val workName = backStackEntry.arguments?.getString("workName") ?: "Unknown"
            ProjectScreen(
                workName = workName,
                onEdit = { navController.navigate("canvas") },
                onExport = { /* Handle export */ }
            )
        }
    }
}
