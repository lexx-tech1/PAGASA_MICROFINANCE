package com.pagasa.microfinance.ui.theme

import android.app.Activity
import android.os.Build
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

private val LightColors = lightColorScheme(
    primary = PagasaBlue,
    onPrimary = Color.White,
    secondary = PagasaGreen,
    onSecondary = Color.White,
    tertiary = PagasaWarning,
    background = PagasaBackground,
    surface = Color.White,
    surfaceVariant = PagasaSky,
    onBackground = PagasaText,
    onSurface = PagasaText,
    error = PagasaError
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FC7FF),
    onPrimary = Color(0xFF00345F),
    secondary = Color(0xFF6CE0B7),
    onSecondary = Color(0xFF003827),
    background = PagasaDarkBackground,
    surface = Color(0xFF0D2945),
    surfaceVariant = Color(0xFF183A5B),
    onBackground = Color(0xFFE8F1FA),
    onSurface = Color(0xFFE8F1FA),
    error = Color(0xFFFFB4AB)
)

@Composable
fun PagasaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colors, typography = PagasaTypography, content = content)
}
