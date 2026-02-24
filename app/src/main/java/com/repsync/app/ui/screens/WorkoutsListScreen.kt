package com.repsync.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.data.entity.WorkoutWithExercises
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.BackgroundPrimary
import com.repsync.app.ui.theme.BackgroundSurface
import com.repsync.app.ui.theme.DestructiveRed
import com.repsync.app.ui.theme.Divider
import com.repsync.app.ui.theme.InputBackground
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.WorkoutsListViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun WorkoutsListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNewWorkout: () -> Unit,
    onNavigateToEditWorkout: (Long) -> Unit,
    onStartWorkout: (Long) -> Unit,
    viewModel: WorkoutsListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Filter workouts based on search query
    val filteredWorkouts = if (uiState.searchQuery.isBlank()) {
        uiState.workouts
    } else {
        uiState.workouts.filter {
            it.workout.name.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary),
        ) {
            // Header
            WorkoutsHeader(
                isSearchVisible = uiState.isSearchVisible,
                searchQuery = uiState.searchQuery,
                onBackClick = onNavigateBack,
                onSearchToggle = viewModel::toggleSearch,
                onSearchQueryChange = viewModel::onSearchQueryChange,
            )

            // Workout list
            if (filteredWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "No workouts found"
                        else "No workouts yet.\nTap + to create one.",
                        color = TextOnDarkSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            } else if (uiState.searchQuery.isNotBlank()) {
                // Non-reorderable list when searching
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(
                        items = filteredWorkouts,
                        key = { it.workout.id }
                    ) { workout ->
                        WorkoutListItem(
                            workout = workout,
                            onClick = { viewModel.selectWorkout(workout) },
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                // Reorderable list when not searching
                val lazyListState = rememberLazyListState()
                val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    // Offset by 1 for the header spacer item
                    viewModel.moveWorkout(from.index - 1, to.index - 1)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    itemsIndexed(
                        items = filteredWorkouts,
                        key = { _, workout -> workout.workout.id }
                    ) { _, workout ->
                        ReorderableItem(reorderableLazyListState, key = workout.workout.id) { isDragging ->
                            WorkoutListItem(
                                workout = workout,
                                onClick = { viewModel.selectWorkout(workout) },
                                modifier = Modifier
                                    .longPressDraggableHandle(
                                        onDragStopped = { viewModel.saveWorkoutOrder() },
                                    )
                                    .graphicsLayer {
                                        alpha = if (isDragging) 0.85f else 1f
                                    }
                                    .then(
                                        if (isDragging) Modifier
                                            .zIndex(1f)
                                            .shadow(8.dp, RoundedCornerShape(12.dp))
                                        else Modifier
                                    ),
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // FAB "+" button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(BackgroundCardElevated)
                .clickable { onNavigateToNewWorkout() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = TextOnDark,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
            )
        }

        // Workout detail overlay
        AnimatedVisibility(
            visible = uiState.selectedWorkout != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            uiState.selectedWorkout?.let { workout ->
                WorkoutDetailOverlay(
                    workout = workout,
                    onDismiss = viewModel::dismissWorkoutDetail,
                    onStartWorkout = { onStartWorkout(workout.workout.id) },
                    onEditWorkout = { onNavigateToEditWorkout(workout.workout.id) },
                    onDeleteWorkout = { viewModel.deleteWorkout(workout.workout) },
                )
            }
        }
    }
}

@Composable
private fun WorkoutsHeader(
    isSearchVisible: Boolean,
    searchQuery: String,
    onBackClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "Workouts",
                color = TextOnDark,
                style = MaterialTheme.typography.headlineLarge,
            )

            // Search button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundCardElevated)
                    .clickable { onSearchToggle() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isSearchVisible) "x" else "?",
                    color = TextOnDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Search bar
        AnimatedVisibility(visible = isSearchVisible) {
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InputBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextOnDark),
                singleLine = true,
                cursorBrush = SolidColor(PrimaryGreen),
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search workouts...",
                                color = TextOnDarkSecondary,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun WorkoutListItem(
    workout: WorkoutWithExercises,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text(
            text = workout.workout.name,
            color = TextOnDark,
            style = MaterialTheme.typography.titleMedium,
        )
        if (workout.exercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${workout.exercises.size} exercise${if (workout.exercises.size != 1) "s" else ""}",
                color = TextOnDarkSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun WorkoutDetailOverlay(
    workout: WorkoutWithExercises,
    onDismiss: () -> Unit,
    onStartWorkout: () -> Unit,
    onEditWorkout: () -> Unit,
    onDeleteWorkout: () -> Unit,
) {
    // Semi-transparent background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center,
    ) {
        // Modal card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BackgroundCard)
                .clickable(enabled = false) {} // Prevent click-through
                .padding(20.dp),
        ) {
            // Header: X button + workout name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // X close button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BackgroundCardElevated)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "X",
                        color = TextOnDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = workout.workout.name,
                    color = TextOnDark,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )

                // Spacer to balance the X button
                Spacer(modifier = Modifier.width(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Divider, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise list
            val sortedExercises = workout.exercises.sortedBy { it.exercise.orderIndex }
            sortedExercises.forEachIndexed { index, exerciseWithSets ->
                val setCount = exerciseWithSets.sets.size
                Text(
                    text = "$setCount x ${exerciseWithSets.exercise.name}",
                    color = TextOnDark,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
                if (index < sortedExercises.size - 1) {
                    HorizontalDivider(color = Divider, thickness = 0.5.dp)
                }
            }

            if (sortedExercises.isEmpty()) {
                Text(
                    text = "No exercises added yet",
                    color = TextOnDarkSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start Workout button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen)
                    .clickable { onStartWorkout() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Start Workout",
                    color = TextOnDark,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Edit button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCardElevated)
                    .clickable { onEditWorkout() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Edit Workout",
                    color = TextOnDarkSecondary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Delete button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DestructiveRed)
                    .clickable { onDeleteWorkout() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Delete Workout",
                    color = TextOnDark,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
