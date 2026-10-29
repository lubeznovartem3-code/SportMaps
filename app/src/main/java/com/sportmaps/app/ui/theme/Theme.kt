package com.sportmaps.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = White,
    background = White,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray500,
    outline = Gray300,
    outlineVariant = Gray200,
    secondaryContainer = Gray100,
    onSecondaryContainer = Ink,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = Gray50,
    onPrimary = Ink,
    background = DarkBackground,
    onBackground = Gray100,
    surface = DarkSurface,
    onSurface = Gray100,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Gray100,
    error = Color(0xFFF87171)
)

/** Тема приложения (Material 3) со светлой и тёмной палитрой. */
@Composable
fun SportMapsTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
