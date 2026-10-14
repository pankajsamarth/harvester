package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.ActiveTimerScreen
import com.example.myapplication.ui.screens.AddJobScreen
import com.example.myapplication.ui.screens.DashboardScreen
import com.example.myapplication.ui.screens.JobDetailsScreen
import com.example.myapplication.viewmodel.HarvestViewModel

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddJob : Screen("add_job")
    object ActiveTimer : Screen("active_timer/{jobId}") {
        fun createRoute(jobId: Int) = "active_timer/$jobId"
    }
    object JobDetails : Screen("job_details/{jobId}") {
        fun createRoute(jobId: Int) = "job_details/$jobId"
    }
}

@Composable
fun HarvesterNavGraph(
    viewModel: HarvestViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddJob = {
                    navController.navigate(Screen.AddJob.route)
                },
                onNavigateToActiveTimer = { jobId ->
                    navController.navigate(Screen.ActiveTimer.createRoute(jobId))
                },
                onNavigateToJobDetails = { jobId ->
                    navController.navigate(Screen.JobDetails.createRoute(jobId))
                }
            )
        }

        composable(Screen.AddJob.route) {
            AddJobScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onJobStarted = { newJobId ->
                    navController.navigate(Screen.ActiveTimer.createRoute(newJobId.toInt())) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.ActiveTimer.route,
            arguments = listOf(
                navArgument("jobId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            ActiveTimerScreen(
                jobId = jobId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onJobCompleted = { completedJobId ->
                    navController.navigate(Screen.JobDetails.createRoute(completedJobId)) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.JobDetails.route,
            arguments = listOf(
                navArgument("jobId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            JobDetailsScreen(
                jobId = jobId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
