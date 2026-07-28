package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Tailwind Light Modern Palette
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate600 = Color(0xFF475569)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)

val Teal600 = Color(0xFF0D9488)
val Teal50 = Color(0xFFF0FDFA)
val Teal100 = Color(0xFFCCFBF1)
val Teal700 = Color(0xFF0F766E)

val Indigo600 = Color(0xFF4F46E5)
val Indigo50 = Color(0xFFEEF2FF)
val Indigo100 = Color(0xFFE0E7FF)

val Amber500 = Color(0xFFF59E0B)
val Rose500 = Color(0xFFF43F5E)
val Emerald500 = Color(0xFF10B981)

// Match Highlight Colors (Color pairs for text background / border)
data class HighlightColorPair(val bg: Color, val text: Color, val border: Color)

val MatchHighlights = listOf(
    HighlightColorPair(Color(0xFFFEF08A), Color(0xFF854D0E), Color(0xFFFDE047)), // Amber
    HighlightColorPair(Color(0xFFA5F3FC), Color(0xFF155E75), Color(0xFF67E8F9)), // Cyan
    HighlightColorPair(Color(0xFFA7F3D0), Color(0xFF065F46), Color(0xFF6EE7B7)), // Emerald
    HighlightColorPair(Color(0xFFFECDD3), Color(0xFF9F1239), Color(0xFFFDA4AF)), // Rose
    HighlightColorPair(Color(0xFFE9D5FF), Color(0xFF6B21A8), Color(0xFFD8B4FE))  // Purple
)
