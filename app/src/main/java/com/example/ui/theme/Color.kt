package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Bento Grid Theme Palette
val BentoPurplePrimary = Color(0xFF6750A4)
val BentoPurpleLight = Color(0xFFD0BCFF)
val BentoPurpleContainer = Color(0xFFE8DEF8)
val BentoPurpleDark = Color(0xFF21005D)
val BentoPurpleDeep = Color(0xFF381E72)

// Bento Accents & Avatars
val BentoStreaksDot1 = Color(0xFFF2B8B5) // Coral Rose
val BentoStreaksDot2 = Color(0xFFB2D7FF) // Soft Sky
val BentoStreaksDot3 = Color(0xFFC2E7FF) // Soft Cyan
val BentoTeal = Color(0xFF006A60)
val BentoAmber = Color(0xFFE08600)
val BentoRose = Color(0xFFB3261E)

// Light Palette (Bento Warm Cream Canvas)
val LightBackground = Color(0xFFFDF8F6)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF3EDF7)
val LightSurfaceCard = Color(0xFFFEF7FF)
val LightOnBackground = Color(0xFF1D1B20)
val LightOnSurface = Color(0xFF1D1B20)
val LightOnSurfaceVariant = Color(0xFF49454F)
val LightOutline = Color(0xFFCAC4D0)
val LightOutlineVariant = Color(0xFFE8DEF8)

// Dark Palette (Bento Night Canvas)
val DarkBackground = Color(0xFF141218)
val DarkSurface = Color(0xFF1E1A22)
val DarkSurfaceVariant = Color(0xFF2B2333)
val DarkSurfaceCard = Color(0xFF251F2C)
val DarkOnBackground = Color(0xFFE6E0E9)
val DarkOnSurface = Color(0xFFE6E0E9)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)
val DarkOutline = Color(0xFF49454F)
val DarkOutlineVariant = Color(0xFF332D3B)

// Brand Accents compatibility
val PrimaryIndigo = BentoPurplePrimary
val PrimaryIndigoLight = BentoPurpleLight
val AccentEmerald = Color(0xFF2E7D32)
val AccentTeal = BentoTeal
val AccentAmber = BentoAmber
val AccentRose = BentoRose

// Color palette options for habit styling
val HabitColorOptions = listOf(
    "#6750A4", // Bento Purple
    "#006A60", // Deep Teal
    "#7D5260", // Plum Mauve
    "#34618E", // Steel Blue
    "#E06D53", // Terracotta
    "#795900", // Warm Bronze
    "#984061", // Berry Rose
    "#2E7D32", // Forest Emerald
    "#5B6400", // Olive Sage
    "#B3261E"  // Carmine
)

// Curated icon emojis for habits
val HabitIconOptions = listOf(
    "📚", "🏃", "💧", "🧘", "📖", "💪", "🥗", "🎯",
    "🚶", "💤", "🍵", "✍️", "🧹", "🎹", "💊", "🌿",
    "🎨", "🚴", "🍎", "⏰", "💻", "🧠", "✨", "🎵"
)

fun parseHexColor(hex: String, defaultColor: Color = BentoPurplePrimary): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color((0xFF000000 or colorInt))
        } else if (cleanHex.length == 8) {
            Color(colorInt)
        } else {
            defaultColor
        }
    } catch (e: Exception) {
        defaultColor
    }
}
