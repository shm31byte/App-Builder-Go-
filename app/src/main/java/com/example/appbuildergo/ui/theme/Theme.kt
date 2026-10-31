package com.example.appbuildergo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PremiumDarkColors = darkColorScheme(
    primary = NeonPurple,
    onPrimary = TextPrimary,
    primaryContainer = NeonPurpleDark,
    onPrimaryContainer = TextPrimary,
    secondary = NeonPurple,
    onSecondary = TextPrimary,
    background = DeepSpaceBlack,
    onBackground = TextPrimary,
    surface = DarkGlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkGlassSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorder
)

@Composable
fun AppBuilderGoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PremiumDarkColors,
        typography = AppTypography,
        content = content
    )
}