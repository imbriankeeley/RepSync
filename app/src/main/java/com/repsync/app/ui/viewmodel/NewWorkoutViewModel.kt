package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.entity.ExerciseEntity
import com.repsync.app.data.entity.ExerciseSetEntity
import com.repsync.app.data.entity.PreviousSetResult
import com.repsync.app.data.entity.WorkoutEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SetUiModel(
    val orderIndex: Int,
    val weight: String = "",
    val reps: String = "",
    val previous: PreviousSetResult? = null,
)

data class ExerciseUiModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val sets: List<SetUiModel> = listOf(SetUiModel(orderIndex = 0)),
)

data class NewWorkoutUiState(
    val workoutName: String = "",
    val exercises: List<ExerciseUiModel> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val editingWorkoutId: Long? = null,
    val exerciseNameSuggestions: List<String> = emptyList(),
)

class NewWorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutDao = RepSyncDatabase.getDatabase(application).workoutDao()
    private val completedWorkoutDao = RepSyncDatabase.getDatabase(application).completedWorkoutDao()

    private val _uiState = MutableStateFlow(NewWorkoutUiState())
    val uiState: StateFlow<NewWorkoutUiState> = _uiState.asStateFlow()

    init {
        loadExerciseNames()
    }

    private fun loadExerciseNames() {
        viewModelScope.launch {
            val names = completedWorkoutDao.getAllExerciseNames()
            _uiState.value = _uiState.value.copy(exerciseNameSuggestions = names)
        }
    }

    fun loadWorkoutForEditing(workoutId: Long) {
        viewModelScope.launch {
            val workoutWithExercises = workoutDao.getWorkoutWithExercises(workoutId) ?: return@launch
            val exercises = workoutWithExercises.exercises
                .sortedBy { it.exercise.orderIndex }
                .map { exerciseWithSets ->
                    val previousSets = completedWorkoutDao.getAllPreviousSetsForExercise(
                        exerciseWithSets.exercise.name
                    )
                    ExerciseUiModel(
                        name = exerciseWithSets.exercise.name,
                        sets = exerciseWithSets.sets
                            .sortedBy { it.orderIndex }
                            .mapIndexed { index, set ->
                                SetUiModel(
                                    orderIndex = set.orderIndex,
                                    weight = set.weight?.let { formatWeight(it) } ?: "",
                                    reps = set.reps?.toString() ?: "",
                                    previous = previousSets.getOrNull(index),
                                )
                            }.ifEmpty {
                                listOf(SetUiModel(orderIndex = 0))
                            },
                    )
                }
            _uiState.value = _uiState.value.copy(
                workoutName = workoutWithExercises.workout.name,
                exercises = exercises,
                editingWorkoutId = workoutId,
            )
        }
    }

    fun onWorkoutNameChange(name: String) {
        _uiState.value = _uiState.value.copy(workoutName = name)
    }

    fun addExercise() {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises + ExerciseUiModel()
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
        // Load previous data when exercise name changes
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

    fun addSet(exerciseId: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val newIndex = exercise.sets.size
                    // Try to get previous data for the new set
                    exercise.copy(
                        sets = exercise.sets + SetUiModel(orderIndex = newIndex)
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

    fun onSetWeightChange(exerciseId: String, setIndex: Int, weight: String) {
        updateSet(exerciseId, setIndex) { it.copy(weight = weight) }
    }

    fun onSetRepsChange(exerciseId: String, setIndex: Int, reps: String) {
        updateSet(exerciseId, setIndex) { it.copy(reps = reps) }
    }

    private fun updateSet(exerciseId: String, setIndex: Int, transform: (SetUiModel) -> SetUiModel) {
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

    fun saveWorkout() {
        val state = _uiState.value
        if (state.workoutName.isBlank()) return
        if (state.exercises.isEmpty()) return

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            val editId = state.editingWorkoutId
            if (editId != null) {
                // Update existing workout
                workoutDao.updateWorkout(WorkoutEntity(id = editId, name = state.workoutName))
                workoutDao.deleteExercisesByWorkoutId(editId)
                insertExercisesAndSets(editId, state.exercises)
            } else {
                // Create new workout
                val workoutId = workoutDao.insertWorkout(
                    WorkoutEntity(name = state.workoutName)
                )
                insertExercisesAndSets(workoutId, state.exercises)
            }
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    private suspend fun insertExercisesAndSets(workoutId: Long, exercises: List<ExerciseUiModel>) {
        exercises.forEachIndexed { exerciseIndex, exercise ->
            if (exercise.name.isBlank()) return@forEachIndexed
            val exerciseId = workoutDao.insertExercise(
                ExerciseEntity(
                    workoutId = workoutId,
                    name = exercise.name,
                    orderIndex = exerciseIndex,
                )
            )
            val sets = exercise.sets.mapIndexed { setIndex, set ->
                ExerciseSetEntity(
                    exerciseId = exerciseId,
                    orderIndex = setIndex,
                    weight = set.weight.toDoubleOrNull(),
                    reps = set.reps.toIntOrNull(),
                )
            }
            if (sets.isNotEmpty()) {
                workoutDao.insertSets(sets)
            }
        }
    }

    private fun formatWeight(weight: Double): String {
        return if (weight == weight.toLong().toDouble()) {
            weight.toLong().toString()
        } else {
            weight.toString()
        }
    }
}
