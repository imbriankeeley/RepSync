package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.entity.CompletedExerciseEntity
import com.repsync.app.data.entity.CompletedSetEntity
import com.repsync.app.data.entity.CompletedWorkoutEntity
import com.repsync.app.data.entity.PreviousSetResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ActiveSetUiModel(
    val orderIndex: Int,
    val weight: String = "",
    val reps: String = "",
    val previous: PreviousSetResult? = null,
    val isCompleted: Boolean = false,
)

data class ActiveExerciseUiModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val sets: List<ActiveSetUiModel> = listOf(ActiveSetUiModel(orderIndex = 0)),
)

data class ActiveWorkoutUiState(
    val workoutName: String = "",
    val exercises: List<ActiveExerciseUiModel> = emptyList(),
    val elapsedSeconds: Long = 0,
    val isLoading: Boolean = true,
    val showCancelDialog: Boolean = false,
    val showFinishDialog: Boolean = false,
    val isFinished: Boolean = false,
    val isCancelled: Boolean = false,
    val templateId: Long? = null,
)

class ActiveWorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutDao = RepSyncDatabase.getDatabase(application).workoutDao()
    private val completedWorkoutDao = RepSyncDatabase.getDatabase(application).completedWorkoutDao()

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startedAtMillis: Long = 0L

    fun loadWorkout(workoutId: Long) {
        startedAtMillis = System.currentTimeMillis()
        startTimer()

        viewModelScope.launch {
            val workoutWithExercises = workoutDao.getWorkoutWithExercises(workoutId)
                ?: return@launch

            val exercises = workoutWithExercises.exercises
                .sortedBy { it.exercise.orderIndex }
                .map { exerciseWithSets ->
                    val previousSets = completedWorkoutDao.getAllPreviousSetsForExercise(
                        exerciseWithSets.exercise.name
                    )
                    ActiveExerciseUiModel(
                        name = exerciseWithSets.exercise.name,
                        sets = exerciseWithSets.sets
                            .sortedBy { it.orderIndex }
                            .mapIndexed { index, set ->
                                ActiveSetUiModel(
                                    orderIndex = set.orderIndex,
                                    weight = set.weight?.let { formatWeight(it) } ?: "",
                                    reps = set.reps?.toString() ?: "",
                                    previous = previousSets.getOrNull(index),
                                )
                            }.ifEmpty {
                                listOf(ActiveSetUiModel(orderIndex = 0))
                            },
                    )
                }

            _uiState.value = _uiState.value.copy(
                workoutName = workoutWithExercises.workout.name,
                exercises = exercises,
                isLoading = false,
                templateId = workoutId,
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val elapsed = (System.currentTimeMillis() - startedAtMillis) / 1000
                _uiState.value = _uiState.value.copy(elapsedSeconds = elapsed)
            }
        }
    }

    fun toggleSetCompleted(exerciseId: String, setIndex: Int) {
        updateSet(exerciseId, setIndex) { it.copy(isCompleted = !it.isCompleted) }
    }

    fun onSetWeightChange(exerciseId: String, setIndex: Int, weight: String) {
        updateSet(exerciseId, setIndex) { it.copy(weight = weight) }
    }

    fun onSetRepsChange(exerciseId: String, setIndex: Int, reps: String) {
        updateSet(exerciseId, setIndex) { it.copy(reps = reps) }
    }

    fun addSet(exerciseId: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val newIndex = exercise.sets.size
                    exercise.copy(
                        sets = exercise.sets + ActiveSetUiModel(orderIndex = newIndex)
                    )
                } else exercise
            }
        )
        // Load previous for the newly added set
        val exercise = _uiState.value.exercises.find { it.id == exerciseId }
        if (exercise != null && exercise.name.isNotBlank()) {
            viewModelScope.launch {
                val newSetIndex = exercise.sets.size - 1
                val previous = completedWorkoutDao.getPreviousSetForExercise(
                    exercise.name, newSetIndex
                )
                if (previous != null) {
                    val updated = _uiState.value
                    _uiState.value = updated.copy(
                        exercises = updated.exercises.map { ex ->
                            if (ex.id == exerciseId) {
                                ex.copy(
                                    sets = ex.sets.mapIndexed { index, set ->
                                        if (index == newSetIndex) set.copy(previous = previous)
                                        else set
                                    }
                                )
                            } else ex
                        }
                    )
                }
            }
        }
    }

    fun removeSet(exerciseId: String, setIndex: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id == exerciseId && exercise.sets.size > 1) {
                    exercise.copy(
                        sets = exercise.sets.filterIndexed { index, _ -> index != setIndex }
                            .mapIndexed { index, set -> set.copy(orderIndex = index) }
                    )
                } else exercise
            }
        )
    }

    fun addExercise() {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises + ActiveExerciseUiModel()
        )
    }

    fun removeExercise(exerciseId: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.filter { it.id != exerciseId }
        )
    }

    fun onExerciseNameChange(exerciseId: String, name: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id == exerciseId) exercise.copy(name = name)
                else exercise
            }
        )
        loadPreviousData(exerciseId, name)
    }

    private fun loadPreviousData(exerciseId: String, exerciseName: String) {
        if (exerciseName.isBlank()) return
        viewModelScope.launch {
            val previousSets = completedWorkoutDao.getAllPreviousSetsForExercise(exerciseName)
            if (previousSets.isNotEmpty()) {
                val current = _uiState.value
                _uiState.value = current.copy(
                    exercises = current.exercises.map { exercise ->
                        if (exercise.id == exerciseId) {
                            exercise.copy(
                                sets = exercise.sets.mapIndexed { index, set ->
                                    set.copy(previous = previousSets.getOrNull(index))
                                }
                            )
                        } else exercise
                    }
                )
            }
        }
    }

    fun showCancelDialog() {
        _uiState.value = _uiState.value.copy(showCancelDialog = true)
    }

    fun dismissCancelDialog() {
        _uiState.value = _uiState.value.copy(showCancelDialog = false)
    }

    fun cancelWorkout() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            showCancelDialog = false,
            isCancelled = true,
        )
    }

    fun showFinishDialog() {
        _uiState.value = _uiState.value.copy(showFinishDialog = true)
    }

    fun dismissFinishDialog() {
        _uiState.value = _uiState.value.copy(showFinishDialog = false)
    }

    fun finishWorkout() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(showFinishDialog = false)

        viewModelScope.launch {
            val state = _uiState.value
            val now = System.currentTimeMillis()
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // Insert completed workout
            val completedWorkoutId = completedWorkoutDao.insertCompletedWorkout(
                CompletedWorkoutEntity(
                    name = state.workoutName,
                    templateId = state.templateId,
                    date = today,
                    startedAt = startedAtMillis,
                    endedAt = now,
                    isQuickWorkout = false,
                )
            )

            // Insert completed exercises and sets
            state.exercises.forEachIndexed { exerciseIndex, exercise ->
                if (exercise.name.isBlank()) return@forEachIndexed
                val completedExerciseId = completedWorkoutDao.insertCompletedExercise(
                    CompletedExerciseEntity(
                        completedWorkoutId = completedWorkoutId,
                        name = exercise.name,
                        orderIndex = exerciseIndex,
                    )
                )
                val completedSets = exercise.sets.mapIndexed { setIndex, set ->
                    CompletedSetEntity(
                        completedExerciseId = completedExerciseId,
                        orderIndex = setIndex,
                        weight = set.weight.toDoubleOrNull(),
                        reps = set.reps.toIntOrNull(),
                    )
                }
                if (completedSets.isNotEmpty()) {
                    completedWorkoutDao.insertCompletedSets(completedSets)
                }
            }

            _uiState.value = _uiState.value.copy(isFinished = true)
        }
    }

    private fun updateSet(
        exerciseId: String,
        setIndex: Int,
        transform: (ActiveSetUiModel) -> ActiveSetUiModel,
    ) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(
                        sets = exercise.sets.mapIndexed { index, set ->
                            if (index == setIndex) transform(set)
                            else set
                        }
                    )
                } else exercise
            }
        )
    }

    private fun formatWeight(weight: Double): String {
        return if (weight == weight.toLong().toDouble()) {
            weight.toLong().toString()
        } else {
            weight.toString()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
