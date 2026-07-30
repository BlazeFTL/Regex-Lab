package com.example.model

import androidx.compose.ui.graphics.Color

data class AppTheme(
    val id: String,
    val name: String,
    val isGradient: Boolean,
    val primaryColor: Color,
    val primaryContainer: Color,
    val gradientColors: List<Color>,
    val onPrimaryContainer: Color
)

object AppThemeData {
    val StaticThemes = listOf(
        AppTheme(
            id = "teal",
            name = "Teal Teal",
            isGradient = false,
            primaryColor = Color(0xFF0D9488),
            primaryContainer = Color(0xFFCCFBF1),
            gradientColors = listOf(Color(0xFF0D9488), Color(0xFF0D9488)),
            onPrimaryContainer = Color(0xFF0F766E)
        ),
        AppTheme(
            id = "blue",
            name = "Ocean Blue",
            isGradient = false,
            primaryColor = Color(0xFF0284C7),
            primaryContainer = Color(0xFFE0F2FE),
            gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0284C7)),
            onPrimaryContainer = Color(0xFF0369A1)
        ),
        AppTheme(
            id = "purple",
            name = "Royal Purple",
            isGradient = false,
            primaryColor = Color(0xFF7C3AED),
            primaryContainer = Color(0xFFF3E8FF),
            gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF7C3AED)),
            onPrimaryContainer = Color(0xFF6D28D9)
        ),
        AppTheme(
            id = "amber",
            name = "Sunset Amber",
            isGradient = false,
            primaryColor = Color(0xFFD97706),
            primaryContainer = Color(0xFFFEF3C7),
            gradientColors = listOf(Color(0xFFD97706), Color(0xFFD97706)),
            onPrimaryContainer = Color(0xFFB45309)
        ),
        AppTheme(
            id = "rose",
            name = "Crimson Rose",
            isGradient = false,
            primaryColor = Color(0xFFE11D48),
            primaryContainer = Color(0xFFFFE4E6),
            gradientColors = listOf(Color(0xFFE11D48), Color(0xFFE11D48)),
            onPrimaryContainer = Color(0xFFBE123C)
        ),
        AppTheme(
            id = "emerald",
            name = "Dark Emerald",
            isGradient = false,
            primaryColor = Color(0xFF059669),
            primaryContainer = Color(0xFFD1FAE5),
            gradientColors = listOf(Color(0xFF059669), Color(0xFF059669)),
            onPrimaryContainer = Color(0xFF047857)
        )
    )

    val MixedThemes = listOf(
        AppTheme(
            id = "gradient_teal_indigo",
            name = "Teal & Indigo",
            isGradient = true,
            primaryColor = Color(0xFF0D9488),
            primaryContainer = Color(0xFFCCFBF1),
            gradientColors = listOf(Color(0xFF0D9488), Color(0xFF4F46E5)),
            onPrimaryContainer = Color(0xFF0F766E)
        ),
        AppTheme(
            id = "gradient_cyan_pink",
            name = "Cyan & Pink",
            isGradient = true,
            primaryColor = Color(0xFF06B6D4),
            primaryContainer = Color(0xFFCFFAFE),
            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFFEC4899)),
            onPrimaryContainer = Color(0xFF0E7490)
        ),
        AppTheme(
            id = "gradient_sunset_rose",
            name = "Sunset & Rose",
            isGradient = true,
            primaryColor = Color(0xFFF97316),
            primaryContainer = Color(0xFFFFEDD5),
            gradientColors = listOf(Color(0xFFF97316), Color(0xFFE11D48)),
            onPrimaryContainer = Color(0xFFC2410C)
        ),
        AppTheme(
            id = "gradient_emerald_lime",
            name = "Emerald & Lime",
            isGradient = true,
            primaryColor = Color(0xFF10B981),
            primaryContainer = Color(0xFFD1FAE5),
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF84CC16)),
            onPrimaryContainer = Color(0xFF047857)
        ),
        AppTheme(
            id = "gradient_indigo_purple",
            name = "Indigo & Purple",
            isGradient = true,
            primaryColor = Color(0xFF6366F1),
            primaryContainer = Color(0xFFE0E7FF),
            gradientColors = listOf(Color(0xFF6366F1), Color(0xFFA855F7)),
            onPrimaryContainer = Color(0xFF4338CA)
        ),
        AppTheme(
            id = "gradient_midnight_teal",
            name = "Midnight & Teal",
            isGradient = true,
            primaryColor = Color(0xFF1E40AF),
            primaryContainer = Color(0xFFDBEAFE),
            gradientColors = listOf(Color(0xFF1E40AF), Color(0xFF0D9488)),
            onPrimaryContainer = Color(0xFF1E3A8A)
        )
    )

    val AllThemes = StaticThemes + MixedThemes

    fun getThemeById(id: String): AppTheme {
        return AllThemes.firstOrNull { it.id == id } ?: MixedThemes.first { it.id == "gradient_indigo_purple" }
    }
}

data class AppSettings(
    val themeId: String = "gradient_indigo_purple",
    val hideCheatSheet: Boolean = false,
    val hideTutorials: Boolean = false,
    val hideSaved: Boolean = false,
    val hideAllBottomBar: Boolean = false,
    val hideSaveButton: Boolean = false
)
