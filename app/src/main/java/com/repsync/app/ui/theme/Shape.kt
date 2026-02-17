package com.repsync.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RepSyncShapes = Shapes(
    // Buttons, inputs, small elements
    small = RoundedCornerShape(8.dp),
    // Cards, dialogs
    medium = RoundedCornerShape(12.dp),
    // Large cards, bottom sheets
    large = RoundedCornerShape(16.dp),
    // Extra large (full-screen cards like the main content area)
    extraLarge = RoundedCornerShape(20.dp),
)
