package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ChatGptColorScheme = darkColorScheme(
    primary = ChatGptTextPrimary,
    onPrimary = ChatGptBackground,
    primaryContainer = ChatGptUserBubble,
    onPrimaryContainer = ChatGptTextPrimary,
    secondary = ChatGptAccentGreen,
    onSecondary = ChatGptBackground,
    secondaryContainer = ChatGptChipBg,
    onSecondaryContainer = ChatGptTextPrimary,
    background = ChatGptBackground,
    onBackground = ChatGptTextPrimary,
    surface = ChatGptSurface,
    onSurface = ChatGptTextPrimary,
    surfaceVariant = ChatGptSurfaceElevated,
    onSurfaceVariant = ChatGptTextSecondary,
    outline = ChatGptInputBorder
)

@Composable
fun NexusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChatGptColorScheme,
        typography = Typography,
        content = content
    )
}
