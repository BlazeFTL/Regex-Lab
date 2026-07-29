package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.AppSettings
import com.example.model.AppThemeData

private val LightColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal700,
    secondary = Indigo600,
    onSecondary = Color.White,
    secondaryContainer = Indigo100,
    onSecondaryContainer = Indigo600,
    tertiary = Amber500,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    primaryContainer = Slate800,
    onPrimaryContainer = Teal100,
    secondary = Indigo600,
    onSecondary = Color.White,
    background = Slate900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    surfaceVariant = Slate900,
    onSurfaceVariant = Slate300,
    outline = Slate600
)

@Composable
fun RegexLabTheme(
    settings: AppSettings = AppSettings(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val activeTheme = AppThemeData.getThemeById(settings.themeId)

    val lightScheme = lightColorScheme(
        primary = activeTheme.primaryColor,
        onPrimary = Color.White,
        primaryContainer = activeTheme.primaryContainer,
        onPrimaryContainer = activeTheme.onPrimaryContainer,
        secondary = Indigo600,
        onSecondary = Color.White,
        secondaryContainer = Indigo100,
        onSecondaryContainer = Indigo600,
        tertiary = Amber500,
        background = Slate50,
        onBackground = Slate900,
        surface = Color.White,
        onSurface = Slate900,
        surfaceVariant = Slate100,
        onSurfaceVariant = Slate600,
        outline = Slate300
    )

    val darkScheme = darkColorScheme(
        primary = activeTheme.primaryColor,
        onPrimary = Color.White,
        primaryContainer = Slate800,
        onPrimaryContainer = activeTheme.primaryContainer,
        secondary = Indigo600,
        onSecondary = Color.White,
        background = Slate900,
        onBackground = Slate50,
        surface = Slate800,
        onSurface = Slate50,
        surfaceVariant = Slate900,
        onSurfaceVariant = Slate300,
        outline = Slate600
    )

    val colorScheme = if (darkTheme) darkScheme else lightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
