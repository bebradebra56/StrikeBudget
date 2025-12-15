package com.strikes.busgapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = StrikeBlueLight,
    onPrimary = StrikeTextPrimary,
    secondary = StrikeGold,
    onSecondary = StrikeTextPrimary,
    tertiary = StrikeGoldDark,
    background = StrikeSurfaceDark,
    surface = Color(0xFF1E293B),
    onBackground = StrikeTextDark,
    onSurface = StrikeTextDark,
    error = StrikeError,
    onError = Color.White,
    primaryContainer = StrikeBlue,
    onPrimaryContainer = StrikeTextDark,
    secondaryContainer = StrikeGoldDark,
    onSecondaryContainer = StrikeTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = StrikeBlue,
    onPrimary = Color.White,
    secondary = StrikeGold,
    onSecondary = StrikeTextPrimary,
    tertiary = StrikeGoldDark,
    background = StrikeBackground,
    surface = StrikeSurface,
    onBackground = StrikeTextPrimary,
    onSurface = StrikeTextPrimary,
    error = StrikeError,
    onError = Color.White,
    primaryContainer = StrikeBlueLight,
    onPrimaryContainer = Color.White,
    secondaryContainer = StrikeGoldLight,
    onSecondaryContainer = StrikeTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = StrikeTextSecondary
)

@Composable
fun StrikeBudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}