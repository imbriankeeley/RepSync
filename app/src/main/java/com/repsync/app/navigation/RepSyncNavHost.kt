package com.repsync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.repsync.app.ui.screens.ActiveWorkoutScreen
import com.repsync.app.ui.screens.DayViewScreen
import com.repsync.app.ui.screens.EditProfileScreen
import com.repsync.app.ui.screens.HomeScreen
import com.repsync.app.ui.screens.NewWorkoutScreen
import com.repsync.app.ui.screens.ProfileScreen
import com.repsync.app.ui.screens.WorkoutsListScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    navController.navigate(Screen.DayView.createRoute(dateString))
                },
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
            )
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
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            ActiveWorkoutScreen(
                workoutId = workoutId,
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
            )
        }

        composable(Screen.QuickWorkout.route) {
            ActiveWorkoutScreen(
                workoutId = null,
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
            )
        }

        composable(
            route = Screen.DayView.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType }),
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
            val date = runCatching {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            }.getOrDefault(LocalDate.now())
            DayViewScreen(
                date = date,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
