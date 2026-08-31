package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.local.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = BentoPurpleLight,
    onPrimary = BentoPurpleDark,
    primaryContainer = BentoPurpleDeep,
    onPrimaryContainer = BentoPurpleLight,
    secondary = BentoPurpleContainer,
    onSecondary = BentoPurpleDeep,
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = BentoPurpleContainer,
    tertiary = BentoTeal,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = BentoPurpleContainer,
    onPrimaryContainer = BentoPurpleDeep,
    secondary = BentoPurpleLight,
    onSecondary = BentoPurpleDark,
    secondaryContainer = BentoPurpleContainer,
    onSecondaryContainer = BentoPurpleDeep,
    tertiary = BentoTeal,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

@Composable
fun HabitTrackerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

