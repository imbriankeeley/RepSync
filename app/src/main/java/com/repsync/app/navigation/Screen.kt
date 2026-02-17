package com.repsync.app.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object WorkoutsList : Screen("workouts_list")
    data object QuickWorkout : Screen("quick_workout")
    data object NewWorkout : Screen("new_workout")
    data object EditWorkout : Screen("edit_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "edit_workout/$workoutId"
    }
    data object ActiveWorkout : Screen("active_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "active_workout/$workoutId"
    }
    data object EditProfile : Screen("edit_profile")
}
