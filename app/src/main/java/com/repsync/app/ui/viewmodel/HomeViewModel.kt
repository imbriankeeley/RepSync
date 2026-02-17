package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.WorkoutDaysPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class HomeUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val workoutDates: Set<LocalDate> = emptySet(),
    val currentStreak: Int = 0,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val completedWorkoutDao = RepSyncDatabase.getDatabase(application).completedWorkoutDao()
    private val workoutDaysPrefs = WorkoutDaysPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Cache workout days and dates so we can recalculate streak when either changes
    private var cachedWorkoutDates: Set<LocalDate> = emptySet()
    private var cachedScheduledDays: Set<DayOfWeek> = emptySet()

    init {
        observeWorkoutDates()
        observeWorkoutDays()
    }

    private fun observeWorkoutDates() {
        viewModelScope.launch {
            completedWorkoutDao.getDatesWithCompletedWorkouts().collect { dateStrings ->
                val dates = dateStrings.mapNotNull { str ->
                    runCatching { LocalDate.parse(str) }.getOrNull()
                }.toSet()
                cachedWorkoutDates = dates
                _uiState.value = _uiState.value.copy(
                    workoutDates = dates,
                    currentStreak = calculateStreak(dates, cachedScheduledDays),
                )
            }
        }
    }

    private fun observeWorkoutDays() {
        viewModelScope.launch {
            workoutDaysPrefs.days.collect { days ->
                cachedScheduledDays = days
                _uiState.value = _uiState.value.copy(
                    currentStreak = calculateStreak(cachedWorkoutDates, days),
                )
            }
        }
    }

    /**
     * Calculate workout streak considering scheduled workout days.
     *
     * Rules:
     * - Scheduled days REQUIRE a workout — missing one breaks the streak.
     * - Rest days (non-scheduled) NEVER break the streak, but if the user
     *   worked out on a rest day it counts as a bonus +1.
     * - If no schedule is configured, falls back to simple consecutive-day streak.
     *
     * Examples (schedule Mon–Sat, rest Sun):
     *   Mon–Sun all worked out  → 7-day streak (Sun is bonus)
     *   Tue–Tue all worked out  → 7-day streak
     *   Tue–Tue, missed Sun     → 6-day streak (Sun skipped, not penalised)
     */
    private fun calculateStreak(dates: Set<LocalDate>, scheduledDays: Set<DayOfWeek>): Int {
        if (dates.isEmpty()) return 0
        val today = LocalDate.now()

        if (scheduledDays.isEmpty()) {
            // No schedule configured — fall back to simple consecutive-day streak
            var checkDate = if (dates.contains(today)) today else today.minusDays(1)
            if (!dates.contains(checkDate)) return 0
            var streak = 0
            while (dates.contains(checkDate)) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
            return streak
        }

        // Schedule-aware streak
        var checkDate = today
        val isScheduledToday = scheduledDays.contains(today.dayOfWeek)

        // Determine starting point:
        // If today is a scheduled day and no workout yet, be forgiving — start from yesterday
        // If today is a rest day with no workout, start from yesterday
        // If today has a workout (scheduled or rest day), start from today
        if (!dates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }

        var streak = 0
        while (true) {
            val isScheduled = scheduledDays.contains(checkDate.dayOfWeek)
            val workedOut = dates.contains(checkDate)

            if (isScheduled) {
                // Scheduled day — workout required to continue streak
                if (workedOut) {
                    streak++
                    checkDate = checkDate.minusDays(1)
                } else {
                    // Missed a scheduled day — streak broken
                    break
                }
            } else {
                // Rest day — never breaks the streak
                if (workedOut) {
                    // Bonus: user worked out on a rest day, count it
                    streak++
                }
                checkDate = checkDate.minusDays(1)
            }
        }
        return streak
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
