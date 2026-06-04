package com.example.a210135_nicolechengkee_drnazatul_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a210135_nicolechengkee_drnazatul_project2.ui.theme.Project2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val viewModel: JobViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            JobHomeScreen(navController, viewModel)
        }
        composable(Screen.Jobs.route) {
            JobScreen(navController, viewModel)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController, viewModel)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(Screen.Community.route) {
            CommunityScreen(navController, viewModel)
        }
        composable(
            route = Screen.Scanner.route,
            arguments = listOf(navArgument("source") { type = NavType.StringType })
        ) { back ->
            val source = back.arguments?.getString("source") ?: "apply"
            ResumeScannerScreen(navController, viewModel, source)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { back ->
            val jobId = back.arguments?.getString("jobId") ?: ""
            JobDetailScreen(navController, viewModel, jobId)
        }
        composable(
            route = Screen.Apply.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { back ->
            val jobId = back.arguments?.getString("jobId") ?: ""
            ApplyScreen(navController, viewModel, jobId)
        }
    }
}
