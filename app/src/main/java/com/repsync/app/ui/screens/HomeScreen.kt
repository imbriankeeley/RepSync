package com.repsync.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.ui.components.MotivationalGif
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.CalendarWorkoutDay
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.HomeViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToWorkouts: () -> Unit,
    onNavigateToQuickWorkout: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
    ) {
        // Content area — matches the profile card's extent
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Calendar card + streak
            Column {
                CalendarCard(
                    currentMonth = uiState.currentMonth,
                    workoutDates = uiState.workoutDates,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    onDayClick = onDayClick,
                )

                StreakBadge(streak = uiState.currentStreak)
            }

            // Daily motivational GIF — fills available space between streak and buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                MotivationalGif()
            }

            // Action buttons pinned to the bottom of the content area
            Column {
                ActionButton(
                    text = "Workouts",
                    backgroundColor = BackgroundCard,
                    onClick = onNavigateToWorkouts,
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionButton(
                    text = "Quick Go",
                    backgroundColor = PrimaryGreen,
                    onClick = onNavigateToQuickWorkout,
                )
            }
        }

        // Bottom spacing — matches ProfileScreen's 16dp spacer
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CalendarCard(
    currentMonth: YearMonth,
    workoutDates: Set<LocalDate>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(16.dp),
    ) {
        // Month header with arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onPreviousMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "<<",
                    color = TextOnDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                color = TextOnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNextMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ">>",
                    color = TextOnDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Day-of-week headers
        val dayHeaders = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val firstOfMonth = currentMonth.atDay(1)
        // Sunday = 0 offset for US-style calendar
        val startOffset = firstOfMonth.dayOfWeek.value % 7
        val daysInMonth = currentMonth.lengthOfMonth()
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        // Previous month trailing days
        val prevMonth = currentMonth.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayOfMonth = cellIndex - startOffset + 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            // Previous month trailing days
                            dayOfMonth < 1 -> {
                                val prevDay = prevMonthDays + dayOfMonth
                                Text(
                                    text = "$prevDay",
                                    color = TextOnDarkSecondary.copy(alpha = 0.4f),
                                    fontSize = 16.sp,
                                )
                            }
                            // Next month leading days
                            dayOfMonth > daysInMonth -> {
                                val nextDay = dayOfMonth - daysInMonth
                                Text(
                                    text = "$nextDay",
                                    color = TextOnDarkSecondary.copy(alpha = 0.4f),
                                    fontSize = 16.sp,
                                )
                            }
                            // Current month days
                            else -> {
                                val date = currentMonth.atDay(dayOfMonth)
                                val hasWorkout = workoutDates.contains(date)

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (hasWorkout) {
                                                Modifier.background(
                                                    CalendarWorkoutDay,
                                                    CircleShape,
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable { onDayClick(date) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "$dayOfMonth",
                                        color = if (hasWorkout) TextOnDark else TextOnDark,
                                        fontSize = 16.sp,
                                        fontWeight = if (hasWorkout) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    if (streak <= 0) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "\uD83D\uDD25",
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (streak == 1) "1 Day Streak" else "$streak Day Streak",
            color = TextOnDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = TextOnDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
