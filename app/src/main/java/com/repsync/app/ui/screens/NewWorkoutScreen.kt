package com.repsync.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.BackgroundPrimary
import com.repsync.app.ui.theme.InputBackground
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.ExerciseUiModel
import com.repsync.app.ui.viewmodel.NewWorkoutViewModel
import com.repsync.app.ui.viewmodel.SetUiModel

@Composable
fun NewWorkoutScreen(
    editWorkoutId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: NewWorkoutViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load workout for editing if ID provided
    LaunchedEffect(editWorkoutId) {
        if (editWorkoutId != null && editWorkoutId > 0) {
            viewModel.loadWorkoutForEditing(editWorkoutId)
        }
    }

    // Navigate back after save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        // Header
        NewWorkoutHeader(
            isEditing = editWorkoutId != null && editWorkoutId > 0,
            onBackClick = onNavigateBack,
            onSaveClick = viewModel::saveWorkout,
            canSave = uiState.workoutName.isNotBlank() && uiState.exercises.isNotEmpty(),
        )

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            // Workout name input
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WorkoutNameInput(
                    name = uiState.workoutName,
                    onNameChange = viewModel::onWorkoutNameChange,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Exercise cards
            itemsIndexed(
                items = uiState.exercises,
                key = { _, exercise -> exercise.id },
            ) { _, exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onExerciseNameChange = { name ->
                        viewModel.onExerciseNameChange(exercise.id, name)
                    },
                    onAddSet = { viewModel.addSet(exercise.id) },
                    onRemoveSet = { setIndex -> viewModel.removeSet(exercise.id, setIndex) },
                    onSetWeightChange = { setIndex, weight ->
                        viewModel.onSetWeightChange(exercise.id, setIndex, weight)
                    },
                    onSetRepsChange = { setIndex, reps ->
                        viewModel.onSetRepsChange(exercise.id, setIndex, reps)
                    },
                    onRemoveExercise = { viewModel.removeExercise(exercise.id) },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Add Exercise button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryGreen)
                        .clickable { viewModel.addExercise() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Add Exercise",
                        color = TextOnDark,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NewWorkoutHeader(
    isEditing: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    canSave: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BackgroundCardElevated)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "<",
                color = TextOnDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = if (isEditing) "Edit Workout" else "New Workout",
            color = TextOnDark,
            style = MaterialTheme.typography.headlineLarge,
        )

        // Save button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (canSave) PrimaryGreen else BackgroundCardElevated)
                .clickable(enabled = canSave) { onSaveClick() }
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Save",
                color = if (canSave) TextOnDark else TextOnDarkSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun WorkoutNameInput(
    name: String,
    onNameChange: (String) -> Unit,
) {
    BasicTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextOnDark),
        singleLine = true,
        cursorBrush = SolidColor(PrimaryGreen),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Name: ",
                    color = TextOnDarkSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (name.isEmpty()) {
                        Text(
                            text = "Push",
                            color = TextOnDarkSecondary.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseUiModel,
    onExerciseNameChange: (String) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onSetWeightChange: (Int, String) -> Unit,
    onSetRepsChange: (Int, String) -> Unit,
    onRemoveExercise: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(16.dp),
    ) {
        // Exercise name row with delete button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = exercise.name,
                onValueChange = onExerciseNameChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.titleLarge.copy(color = TextOnDark),
                singleLine = true,
                cursorBrush = SolidColor(PrimaryGreen),
                decorationBox = { innerTextField ->
                    Box {
                        if (exercise.name.isEmpty()) {
                            Text(
                                text = "Exercise name",
                                color = TextOnDarkSecondary.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BackgroundCardElevated)
                    .clickable { onRemoveExercise() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "X",
                    color = TextOnDarkSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            Text(
                text = "Set",
                modifier = Modifier.width(40.dp),
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Previous",
                modifier = Modifier.weight(1f),
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "+lbs",
                modifier = Modifier.weight(1f),
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Reps",
                modifier = Modifier.weight(1f),
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Set rows
        exercise.sets.forEachIndexed { index, set ->
            SetRow(
                setNumber = index + 1,
                set = set,
                onWeightChange = { onSetWeightChange(index, it) },
                onRepsChange = { onSetRepsChange(index, it) },
                onRemove = if (exercise.sets.size > 1) {
                    { onRemoveSet(index) }
                } else null,
            )
            if (index < exercise.sets.size - 1) {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Set button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BackgroundCardElevated)
                .clickable { onAddSet() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+ Add Set",
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun SetRow(
    setNumber: Int,
    set: SetUiModel,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onRemove: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Set number badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(BackgroundCardElevated),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$setNumber",
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        // Previous
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            val previousText = set.previous?.let { prev ->
                val w = prev.weight?.let { formatWeightDisplay(it) } ?: "-"
                val r = prev.reps?.toString() ?: "-"
                "$w x $r"
            } ?: "-"
            Text(
                text = previousText,
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        // Weight input
        SetInputField(
            value = set.weight,
            placeholder = "+lbs",
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Decimal,
        )

        // Reps input
        SetInputField(
            value = set.reps,
            placeholder = "Reps",
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun SetInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = TextOnDark,
            textAlign = TextAlign.Center,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        cursorBrush = SolidColor(PrimaryGreen),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = TextOnDarkSecondary.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
                innerTextField()
            }
        },
    )
}

private fun formatWeightDisplay(weight: Double): String {
    return if (weight == weight.toLong().toDouble()) {
        weight.toLong().toString()
    } else {
        weight.toString()
    }
}
