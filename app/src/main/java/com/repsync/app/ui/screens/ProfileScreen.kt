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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.repsync.app.data.entity.BodyweightEntryEntity
import com.repsync.app.ui.components.ChartDataPoint
import com.repsync.app.ui.components.ProfileAvatar
import com.repsync.app.ui.components.WeightProgressionChart
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.BackgroundPrimary
import com.repsync.app.ui.theme.DestructiveRed
import com.repsync.app.ui.theme.InputBackground
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val displayName = uiState.displayName ?: "Guest"
    val workoutCount = uiState.completedWorkoutCount

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {
            // Main profile card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(16.dp),
            ) {
                // "Profile" header centered
                Text(
                    text = "Profile",
                    modifier = Modifier.fillMaxWidth(),
                    color = TextOnDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Profile row: avatar, name/count, chevron
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToEditProfile() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Avatar (shows saved photo or placeholder icon)
                    ProfileAvatar(
                        avatarPath = uiState.avatarPath,
                        size = 56.dp,
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Name and workout count
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            color = TextOnDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "$workoutCount Workouts",
                            color = TextOnDarkSecondary,
                            fontSize = 16.sp,
                        )
                    }

                    // Chevron
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Edit profile",
                        tint = TextOnDarkSecondary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bodyweight section
            BodyweightSection(
                latestWeight = uiState.latestBodyweight,
                chartData = uiState.bodyweightChartData,
                entries = uiState.bodyweightEntries,
                onAddClick = { viewModel.showAddBodyweightDialog() },
                onDeleteEntry = { viewModel.deleteBodyweightEntry(it) },
                modifier = Modifier.weight(1f),
            )

            // Space for bottom nav
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Add bodyweight dialog overlay
        if (uiState.showAddBodyweightDialog) {
            AddBodyweightDialog(
                onDismiss = { viewModel.dismissAddBodyweightDialog() },
                onSave = { weight -> viewModel.addBodyweightEntry(weight) },
            )
        }
    }
}

@Composable
private fun BodyweightSection(
    latestWeight: Double?,
    chartData: List<ChartDataPoint>,
    entries: List<BodyweightEntryEntity>,
    onAddClick: () -> Unit,
    onDeleteEntry: (BodyweightEntryEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(16.dp),
    ) {
        // Header row: title + latest weight + add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Bodyweight",
                color = TextOnDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (latestWeight != null) {
                    Text(
                        text = "${formatBodyweight(latestWeight)} lbs",
                        color = PrimaryGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        color = TextOnDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chart or placeholder
        if (chartData.size >= 2) {
            WeightProgressionChart(
                dataPoints = chartData,
                label = "lbs",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            )
        } else if (chartData.size == 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Log one more entry to see your chart",
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tap + to log your first bodyweight entry",
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Recent entries list (scrollable, shows most recent first)
        if (entries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recent Entries",
                color = TextOnDarkSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Show entries in reverse chronological order
                entries.reversed().forEach { entry ->
                    BodyweightEntryRow(
                        entry = entry,
                        onDelete = { onDeleteEntry(entry) },
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BodyweightEntryRow(
    entry: BodyweightEntryEntity,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = entry.date,
            color = TextOnDarkSecondary,
            fontSize = 13.sp,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${formatBodyweight(entry.weight)} lbs",
                color = TextOnDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(DestructiveRed.copy(alpha = 0.8f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "X",
                    color = TextOnDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AddBodyweightDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
) {
    var weightText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundCard)
                .clickable { /* Consume clicks so they don't dismiss */ }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Log Bodyweight",
                color = TextOnDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weight input
            BasicTextField(
                value = weightText,
                onValueChange = { newValue ->
                    // Allow digits and one decimal point
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        weightText = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextOnDark,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(PrimaryGreen),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (weightText.isEmpty()) {
                            Text(
                                text = "Weight (lbs)",
                                color = TextOnDarkSecondary,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundCardElevated)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Cancel",
                        color = TextOnDarkSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryGreen)
                        .clickable {
                            val weight = weightText.toDoubleOrNull()
                            if (weight != null && weight > 0) {
                                onSave(weight)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Save",
                        color = TextOnDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private fun formatBodyweight(weight: Double): String {
    return if (weight == weight.toLong().toDouble()) {
        weight.toLong().toString()
    } else {
        "%.1f".format(weight)
    }
}
