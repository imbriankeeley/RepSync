package com.repsync.app.navigation

import android.net.Uri

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
    data object DayView : Screen("day_view/{date}") {
        fun createRoute(date: String) = "day_view/$date"
    }
    data object ExerciseHistory : Screen("exercise_history/{exerciseName}") {
        fun createRoute(exerciseName: String) = "exercise_history/${Uri.encode(exerciseName)}"
    }
    data object BodyweightEntries : Screen("bodyweight_entries")
}
