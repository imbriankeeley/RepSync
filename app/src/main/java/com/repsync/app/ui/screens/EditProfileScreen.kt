package com.repsync.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.ui.components.ProfileAvatar
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.BackgroundPrimary
import com.repsync.app.ui.theme.InputBackground
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.ProfileViewModel
import java.time.DayOfWeek

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameInput by remember(uiState.displayName) {
        mutableStateOf(uiState.displayName ?: "")
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatar(uri)
        }
    }

    // Permission launcher for notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleReminderEnabled()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {
            // Main card — scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Header: Back arrow + title + Save
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Back arrow
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(InputBackground)
                            .clickable { onNavigateBack() }
                            .align(Alignment.CenterStart),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextOnDark,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // Title
                    Text(
                        text = "Edit Profile",
                        modifier = Modifier.align(Alignment.Center),
                        color = TextOnDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    // Save
                    Text(
                        text = "Save",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.updateDisplayName(nameInput)
                                onNavigateBack()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = TextOnDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Profile avatar centered — tap to pick photo
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                    ) {
                        ProfileAvatar(
                            avatarPath = uiState.avatarPath,
                            size = 80.dp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Hint to tap
                Text(
                    text = "Tap to change photo",
                    modifier = Modifier.fillMaxWidth(),
                    color = TextOnDarkSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Display name label
                Text(
                    text = "Display Name",
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Name input field
                BasicTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(InputBackground)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    textStyle = TextStyle(
                        color = TextOnDark,
                        fontSize = 18.sp,
                    ),
                    cursorBrush = SolidColor(PrimaryGreen),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (nameInput.isEmpty()) {
                                Text(
                                    text = "Enter display name",
                                    color = TextOnDarkSecondary,
                                    fontSize = 18.sp,
                                )
                            }
                            innerTextField()
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Workout count info
                Text(
                    text = "${uiState.completedWorkoutCount} Workouts Completed",
                    modifier = Modifier.fillMaxWidth(),
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                SectionDivider()

                // ── Workout Days Section (for streak tracking) ──
                Text(
                    text = "Workout Schedule",
                    color = TextOnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Days you plan to work out. Used for streaks and reminders.",
                    color = TextOnDarkSecondary,
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                DayOfWeekPicker(
                    selectedDays = uiState.workoutDays,
                    onToggleDay = { viewModel.toggleWorkoutDay(it) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                SectionDivider()

                // ── Workout Reminders Section ──
                Text(
                    text = "Workout Reminders",
                    color = TextOnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Reminders",
                        color = TextOnDark,
                        fontSize = 16.sp,
                    )

                    ToggleSwitch(
                        checked = uiState.reminderEnabled,
                        onCheckedChange = {
                            if (!uiState.reminderEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.toggleReminderEnabled()
                            }
                        },
                    )
                }

                if (uiState.reminderEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom message
                    Text(
                        text = "Notification Message",
                        color = TextOnDarkSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var messageInput by remember(uiState.reminderMessage) {
                        mutableStateOf(uiState.reminderMessage)
                    }

                    BasicTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(InputBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        textStyle = TextStyle(
                            color = TextOnDark,
                            fontSize = 16.sp,
                        ),
                        cursorBrush = SolidColor(PrimaryGreen),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (messageInput.isEmpty()) {
                                    Text(
                                        text = "e.g. Push Day!",
                                        color = TextOnDarkSecondary,
                                        fontSize = 16.sp,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )

                    // Auto-save message on change
                    LaunchedEffect(messageInput) {
                        if (messageInput != uiState.reminderMessage && messageInput.isNotBlank()) {
                            kotlinx.coroutines.delay(500) // debounce
                            viewModel.setReminderMessage(messageInput)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time picker row
                    Text(
                        text = "Time",
                        color = TextOnDarkSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundCardElevated)
                            .clickable { viewModel.showTimePicker() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = formatTime(uiState.reminderHour, uiState.reminderMinute),
                            color = TextOnDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Time picker dialog overlay
        if (uiState.showTimePicker) {
            TimePickerDialog(
                currentHour = uiState.reminderHour,
                currentMinute = uiState.reminderMinute,
                onDismiss = { viewModel.dismissTimePicker() },
                onConfirm = { hour, minute -> viewModel.setReminderTime(hour, minute) },
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
) {
    var hourText by remember { mutableStateOf(currentHour.toString()) }
    var minuteText by remember { mutableStateOf(currentMinute.toString().padStart(2, '0')) }

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
                .clickable { /* Consume clicks */ }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Set Reminder Time",
                color = TextOnDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hour and minute inputs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                // Hour input
                BasicTextField(
                    value = hourText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.length <= 2)) {
                            val num = newValue.toIntOrNull()
                            if (num == null || num in 0..23) {
                                hourText = newValue
                            }
                        }
                    },
                    modifier = Modifier
                        .width(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBackground)
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    textStyle = TextStyle(
                        color = TextOnDark,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                    ),
                    cursorBrush = SolidColor(PrimaryGreen),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                Text(
                    text = ":",
                    color = TextOnDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                // Minute input
                BasicTextField(
                    value = minuteText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.length <= 2)) {
                            val num = newValue.toIntOrNull()
                            if (num == null || num in 0..59) {
                                minuteText = newValue
                            }
                        }
                    },
                    modifier = Modifier
                        .width(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBackground)
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    textStyle = TextStyle(
                        color = TextOnDark,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                    ),
                    cursorBrush = SolidColor(PrimaryGreen),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "24-hour format",
                color = TextOnDarkSecondary,
                fontSize = 12.sp,
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
                            val h = hourText.toIntOrNull() ?: currentHour
                            val m = minuteText.toIntOrNull() ?: currentMinute
                            onConfirm(h.coerceIn(0, 23), m.coerceIn(0, 59))
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

private fun formatTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "%d:%02d %s".format(displayHour, minute, period)
}

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BackgroundCardElevated),
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun DayOfWeekPicker(
    selectedDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
) {
    val dayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    val dayValues = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        dayLabels.forEachIndexed { index, label ->
            val day = dayValues[index]
            val isSelected = selectedDays.contains(day)

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) PrimaryGreen
                        else BackgroundCardElevated
                    )
                    .clickable { onToggleDay(day) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = TextOnDark,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (checked) PrimaryGreen else BackgroundCardElevated)
            .clickable { onCheckedChange() },
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(TextOnDark),
        )
    }
}
