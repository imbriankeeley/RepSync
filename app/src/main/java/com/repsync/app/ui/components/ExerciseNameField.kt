package com.repsync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.repsync.app.ui.theme.BackgroundCardElevated
import com.repsync.app.ui.theme.Divider
import com.repsync.app.ui.theme.PrimaryGreen
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary

@Composable
fun ExerciseNameField(
    name: String,
    suggestions: List<String>,
    onNameChange: (String) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    val filtered = remember(name, suggestions) {
        if (name.isBlank()) {
            emptyList()
        } else {
            suggestions.filter {
                it.contains(name, ignoreCase = true) && !it.equals(name, ignoreCase = true)
            }.take(5)
        }
    }

    Column(modifier = modifier) {
        BasicTextField(
            value = name,
            onValueChange = { newName ->
                onNameChange(newName)
                showSuggestions = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (!focusState.isFocused) {
                        showSuggestions = false
                    }
                },
            textStyle = MaterialTheme.typography.titleLarge.copy(color = TextOnDark),
            singleLine = true,
            cursorBrush = SolidColor(PrimaryGreen),
            decorationBox = { innerTextField ->
                Box {
                    if (name.isEmpty()) {
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

        if (showSuggestions && isFocused && filtered.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .zIndex(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundCardElevated),
            ) {
                Column {
                    filtered.forEachIndexed { index, suggestion ->
                        Text(
                            text = suggestion,
                            color = TextOnDark,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSuggestionSelected(suggestion)
                                    showSuggestions = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                        if (index < filtered.size - 1) {
                            HorizontalDivider(
                                color = Divider,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
