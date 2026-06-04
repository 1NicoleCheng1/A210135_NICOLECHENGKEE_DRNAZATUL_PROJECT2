package com.example.a210135_nicolechengkee_drnazatul_project2

sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Jobs      : Screen("jobs")
    object Chat      : Screen("chat")
    object Profile   : Screen("profile")
    object Community : Screen("community")          // NEW – Screen 07
    object Scanner   : Screen("scanner/{source}") { // NEW – Screen 06
        fun createRoute(source: String = "apply") = "scanner/$source"
    }
    object Detail : Screen("detail/{jobId}") {
        fun createRoute(jobId: String) = "detail/$jobId"
    }
    object Apply : Screen("apply/{jobId}") {
        fun createRoute(jobId: String) = "apply/$jobId"
    }
}
