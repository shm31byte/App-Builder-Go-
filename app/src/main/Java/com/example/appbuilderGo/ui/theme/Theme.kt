package com.example.appbuilderGo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.White,
    primaryContainer = PurpleAccentDark,
    onPrimaryContainer = Color.White,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkMuted,
    outline = OnDarkMuted
)

@Composable
fun AppBuilderGoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
