package com.repsync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.repsync.app.ui.screens.HomeScreen
import com.repsync.app.ui.screens.NewWorkoutScreen
import com.repsync.app.ui.screens.PlaceholderScreen
import com.repsync.app.ui.screens.WorkoutsListScreen

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
            WorkoutsListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewWorkout = {
                    navController.navigate(Screen.NewWorkout.route)
                },
                onNavigateToEditWorkout = { workoutId ->
                    navController.navigate(Screen.EditWorkout.createRoute(workoutId))
                },
                onStartWorkout = { workoutId ->
                    // Phase 5: navigate to active workout
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                },
            )
        }

        composable(Screen.NewWorkout.route) {
            NewWorkoutScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.EditWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            NewWorkoutScreen(
                editWorkoutId = workoutId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType }),
        ) {
            // Phase 5: Active Workout screen
            PlaceholderScreen(title = "Active Workout")
        }

        composable(Screen.QuickWorkout.route) {
            PlaceholderScreen(title = "Quick Workout")
        }
    }
}
