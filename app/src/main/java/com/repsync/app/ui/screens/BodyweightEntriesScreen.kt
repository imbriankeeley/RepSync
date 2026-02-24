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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.data.entity.BodyweightEntryEntity
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.BackgroundPrimary
import com.repsync.app.ui.theme.DestructiveRed
import com.repsync.app.ui.theme.InputBackground
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.BodyweightEntriesViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BodyweightEntriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: BodyweightEntriesViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val displayFmt = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundCard)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundCardElevated)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "<",
                    color = TextOnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Bodyweight Entries",
                color = TextOnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val hasFilter = uiState.startDate != null && uiState.endDate != null

            if (hasFilter) {
                Text(
                    text = "${uiState.startDate!!.format(displayFmt)} – ${uiState.endDate!!.format(displayFmt)}",
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DestructiveRed.copy(alpha = 0.8f))
                        .clickable { viewModel.clearDateRange() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Clear",
                        color = TextOnDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                Text(
                    text = "All Time",
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundCardElevated)
                        .clickable { viewModel.showDatePicker() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Filter by Date",
                        color = TextOnDarkSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // Entries list
        if (uiState.filteredEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.startDate != null) "No entries in selected range" else "No entries",
                    color = TextOnDarkSecondary,
                    fontSize = 14.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                items(
                    items = uiState.filteredEntries,
                    key = { it.id },
                ) { entry ->
                    val dateText = runCatching {
                        LocalDate.parse(entry.date).format(displayFmt)
                    }.getOrDefault(entry.date)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundCard)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = dateText,
                            color = TextOnDarkSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )

                        Text(
                            text = "${formatBodyweightEntry(entry.weight)} lbs",
                            color = TextOnDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Edit button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(BackgroundCardElevated)
                                .clickable { viewModel.showEditDialog(entry) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "\u270E",
                                color = TextOnDarkSecondary,
                                fontSize = 14.sp,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(DestructiveRed.copy(alpha = 0.8f))
                                .clickable { viewModel.showDeleteDialog(entry) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "X",
                                color = TextOnDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Edit dialog
    if (uiState.showEditDialog && uiState.editingEntry != null) {
        EditWeightDialog(
            entry = uiState.editingEntry!!,
            onDismiss = { viewModel.dismissEditDialog() },
            onSave = { id, newWeight -> viewModel.updateWeight(id, newWeight) },
        )
    }

    // Delete confirmation dialog
    if (uiState.showDeleteDialog && uiState.deletingEntry != null) {
        DeleteBodyweightEntryDialog(
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = { viewModel.confirmDeleteEntry() },
        )
    }

    // Date picker dialog
    if (uiState.showDatePicker) {
        BodyweightDateRangePickerDialog(
            onDismiss = { viewModel.dismissDatePicker() },
            onConfirm = { startDate, endDate -> viewModel.setDateRange(startDate, endDate) },
        )
    }
}

@Composable
private fun EditWeightDialog(
    entry: BodyweightEntryEntity,
    onDismiss: () -> Unit,
    onSave: (Long, Double) -> Unit,
) {
    var weightText by remember { mutableStateOf(formatBodyweightEntry(entry.weight)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
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
                text = "Edit Weight",
                color = TextOnDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dateText = runCatching {
                LocalDate.parse(entry.date).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            }.getOrDefault(entry.date)
            Text(
                text = dateText,
                color = TextOnDarkSecondary,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            BasicTextField(
                value = weightText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        weightText = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                textStyle = TextStyle(
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
                                onSave(entry.id, weight)
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

@Composable
private fun BodyweightDateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit,
) {
    val today = LocalDate.now()
    var startMonthText by remember { mutableStateOf("1") }
    var startDayText by remember { mutableStateOf("1") }
    var startYearText by remember { mutableStateOf(today.year.toString()) }
    var endMonthText by remember { mutableStateOf(today.monthValue.toString()) }
    var endDayText by remember { mutableStateOf(today.dayOfMonth.toString()) }
    var endYearText by remember { mutableStateOf(today.year.toString()) }

    val startDate = remember(startMonthText, startDayText, startYearText) {
        runCatching {
            LocalDate.of(startYearText.toInt(), startMonthText.toInt(), startDayText.toInt())
        }.getOrNull()
    }
    val endDate = remember(endMonthText, endDayText, endYearText) {
        runCatching {
            LocalDate.of(endYearText.toInt(), endMonthText.toInt(), endDayText.toInt())
        }.getOrNull()
    }

    val isValid = startDate != null && endDate != null && !startDate.isAfter(endDate)

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
                text = "Filter by Date Range",
                color = TextOnDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start date
            Text(
                text = "Start Date",
                color = TextOnDarkSecondary,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            DateInputRow(
                monthText = startMonthText,
                onMonthChange = { startMonthText = it },
                dayText = startDayText,
                onDayChange = { startDayText = it },
                yearText = startYearText,
                onYearChange = { startYearText = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // End date
            Text(
                text = "End Date",
                color = TextOnDarkSecondary,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            DateInputRow(
                monthText = endMonthText,
                onMonthChange = { endMonthText = it },
                dayText = endDayText,
                onDayChange = { endDayText = it },
                yearText = endYearText,
                onYearChange = { endYearText = it },
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preview
            if (isValid) {
                val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
                Text(
                    text = "${startDate!!.format(fmt)} – ${endDate!!.format(fmt)}",
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = "Enter valid start and end dates",
                    color = DestructiveRed,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }

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
                        .background(if (isValid) PrimaryGreen else BackgroundCardElevated)
                        .clickable {
                            if (isValid) {
                                onConfirm(startDate!!, endDate!!)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Apply",
                        color = if (isValid) TextOnDark else TextOnDarkSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun DateInputRow(
    monthText: String,
    onMonthChange: (String) -> Unit,
    dayText: String,
    onDayChange: (String) -> Unit,
    yearText: String,
    onYearChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "MM", color = TextOnDarkSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                value = monthText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() } && newVal.length <= 2)) {
                        val num = newVal.toIntOrNull()
                        if (num == null || num in 0..12) onMonthChange(newVal)
                    }
                },
                modifier = Modifier
                    .width(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                textStyle = TextStyle(
                    color = TextOnDark,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(PrimaryGreen),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
        }

        Text(
            text = "/",
            color = TextOnDarkSecondary,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 6.dp),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "DD", color = TextOnDarkSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                value = dayText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() } && newVal.length <= 2)) {
                        val num = newVal.toIntOrNull()
                        if (num == null || num in 0..31) onDayChange(newVal)
                    }
                },
                modifier = Modifier
                    .width(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                textStyle = TextStyle(
                    color = TextOnDark,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(PrimaryGreen),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
        }

        Text(
            text = "/",
            color = TextOnDarkSecondary,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 6.dp),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "YYYY", color = TextOnDarkSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                value = yearText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() } && newVal.length <= 4)) {
                        onYearChange(newVal)
                    }
                },
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                textStyle = TextStyle(
                    color = TextOnDark,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(PrimaryGreen),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
        }
    }
}

@Composable
private fun DeleteBodyweightEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
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
                text = "Delete Entry?",
                color = TextOnDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This will permanently remove this bodyweight entry.",
                color = TextOnDarkSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        .background(DestructiveRed)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Delete",
                        color = TextOnDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private fun formatBodyweightEntry(weight: Double): String {
    return if (weight == weight.toLong().toDouble()) {
        weight.toLong().toString()
    } else {
        "%.1f".format(weight)
    }
}
