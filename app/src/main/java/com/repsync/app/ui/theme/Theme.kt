package com.repsync.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RepSyncColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = TextOnLight,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = TextOnLight,
    secondary = BackgroundCardElevated,
    onSecondary = TextOnDark,
    secondaryContainer = BackgroundCard,
    onSecondaryContainer = TextOnDark,
    tertiary = DestructiveRed,
    onTertiary = TextOnLight,
    background = BackgroundPrimary,
    onBackground = TextOnDark,
    surface = BackgroundSurface,
    onSurface = TextOnDark,
    surfaceVariant = BackgroundCard,
    onSurfaceVariant = TextOnDarkSecondary,
    outline = Divider,
    error = DestructiveRed,
    onError = TextOnLight,
)

@Composable
fun RepSyncTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RepSyncColorScheme,
        typography = RepSyncTypography,
        shapes = RepSyncShapes,
        content = content,
    )
}
