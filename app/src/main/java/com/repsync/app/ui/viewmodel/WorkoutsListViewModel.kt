package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.entity.WorkoutEntity
import com.repsync.app.data.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutsListUiState(
    val workouts: List<WorkoutWithExercises> = emptyList(),
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val selectedWorkout: WorkoutWithExercises? = null,
)

class WorkoutsListViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutDao = RepSyncDatabase.getDatabase(application).workoutDao()

    private val _uiState = MutableStateFlow(WorkoutsListUiState())
    val uiState: StateFlow<WorkoutsListUiState> = _uiState.asStateFlow()

    init {
        observeWorkouts()
    }

    private fun observeWorkouts() {
        viewModelScope.launch {
            workoutDao.getAllWorkoutsWithExercises().collect { workouts ->
                val current = _uiState.value
                // Refresh selectedWorkout if it's open so detail overlay stays in sync
                val refreshedSelected = current.selectedWorkout?.let { selected ->
                    workouts.find { it.workout.id == selected.workout.id }
                }
                _uiState.value = current.copy(
                    workouts = workouts,
                    selectedWorkout = refreshedSelected,
                )
            }
        }
    }

    fun toggleSearch() {
        val current = _uiState.value
        _uiState.value = current.copy(
            isSearchVisible = !current.isSearchVisible,
            searchQuery = if (current.isSearchVisible) "" else current.searchQuery,
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectWorkout(workout: WorkoutWithExercises) {
        _uiState.value = _uiState.value.copy(selectedWorkout = workout)
    }

    fun dismissWorkoutDetail() {
        _uiState.value = _uiState.value.copy(selectedWorkout = null)
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workout)
            _uiState.value = _uiState.value.copy(selectedWorkout = null)
        }
    }
}
