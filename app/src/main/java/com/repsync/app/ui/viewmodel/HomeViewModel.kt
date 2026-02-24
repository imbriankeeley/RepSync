package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class HomeUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val workoutDates: Set<LocalDate> = emptySet(),
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val completedWorkoutDao = RepSyncDatabase.getDatabase(application).completedWorkoutDao()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeWorkoutDates()
    }

    private fun observeWorkoutDates() {
        viewModelScope.launch {
            completedWorkoutDao.getDatesWithCompletedWorkouts().collect { dateStrings ->
                val dates = dateStrings.mapNotNull { str ->
                    runCatching { LocalDate.parse(str) }.getOrNull()
                }.toSet()
                _uiState.value = _uiState.value.copy(workoutDates = dates)
            }
        }
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.minusMonths(1)
        )
    }

    fun nextMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.plusMonths(1)
        )
    }
}
