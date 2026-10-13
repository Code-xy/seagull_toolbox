package com.seagull.toolbox.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal = Color(0xFF1D9A8A)
private val TealDark = Color(0xFF6BD9C8)

private val Light = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F0EA),
    onPrimaryContainer = Color(0xFF00332C),
    secondary = Color(0xFF4A6B8A),
    background = Color(0xFFF5F6FA),
    onBackground = Color(0xFF1A1C21),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C21),
    surfaceVariant = Color(0xFFEDEFF4),
    onSurfaceVariant = Color(0xFF5A5F6E),
    outline = Color(0xFFC6CAD6),
    outlineVariant = Color(0xFFE3E6EE)
)

private val Dark = darkColorScheme(
    primary = TealDark,
    onPrimary = Color(0xFF00332C),
    primaryContainer = Color(0xFF0F5C52),
    onPrimaryContainer = Color(0xFFD0F0EA),
    secondary = Color(0xFF9DBBD8),
    background = Color(0xFF101216),
    onBackground = Color(0xFFE3E4E9),
    surface = Color(0xFF171A1F),
    onSurface = Color(0xFFE3E4E9),
    surfaceVariant = Color(0xFF242830),
    onSurfaceVariant = Color(0xFFB4BAC6),
    outline = Color(0xFF4A4F5C),
    outlineVariant = Color(0xFF2D3139)
)

@Composable
fun ToolboxTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
