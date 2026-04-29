package com.qiblaarfinder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Forest700,
    onPrimary = Cloud50,
    primaryContainer = Moss300,
    onPrimaryContainer = Ink900,
    secondary = Gold500,
    onSecondary = Cloud50,
    secondaryContainer = Gold200,
    onSecondaryContainer = Ink900,
    tertiary = Forest500,
    onTertiary = Cloud50,
    tertiaryContainer = Sand100,
    onTertiaryContainer = Ink900,
    background = Sand50,
    onBackground = Ink900,
    surface = Cloud50,
    onSurface = Ink900,
    surfaceVariant = Sand100.copy(alpha = 0.55f),
    onSurfaceVariant = Stone700,
    outline = Stone700.copy(alpha = 0.75f),
)

@Composable
fun QiblaARFinderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}

