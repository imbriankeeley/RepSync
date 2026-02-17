package com.repsync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.repsync.app.ui.screens.HomeScreen
import com.repsync.app.ui.screens.PlaceholderScreen

@Composable
fun RepSyncNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToWorkouts = {
                    navController.navigate(Screen.WorkoutsList.route)
                },
                onNavigateToQuickWorkout = {
                    navController.navigate(Screen.QuickWorkout.route)
                },
                onDayClick = { date ->
                    // Phase 8: navigate to day view
                },
            )
        }

        composable(Screen.Profile.route) {
            PlaceholderScreen(title = "Profile")
        }

        composable(Screen.WorkoutsList.route) {
            PlaceholderScreen(title = "Workouts")
        }

        composable(Screen.QuickWorkout.route) {
            PlaceholderScreen(title = "Quick Workout")
        }
    }
}
