package com.repsync.app.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object WorkoutsList : Screen("workouts_list")
    data object QuickWorkout : Screen("quick_workout")
}
