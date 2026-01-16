package com.reportnami.claro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ClaroPrimary,
    background = ClaroDarkBackground,
    surface = ClaroDarkSurface,
    onPrimary = ClaroDarkText,
    onBackground = ClaroDarkText,
    onSurface = ClaroDarkText,
)

private val LightColorScheme = lightColorScheme(
    primary = ClaroPrimary,
    background = ClaroLightBackground,
    surface = ClaroLightSurface,
    onPrimary = ClaroLightText,
    onBackground = ClaroLightText,
    onSurface = ClaroLightText,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val extra = if (darkTheme) {
        ClaroColorsExtra(
            textSecondary = ClaroDarkTextSecondary,
            border = ClaroDarkBorder,
            surfaceAlt = ClaroDarkSurfaceAlt,
            warning = ClaroDarkWarning,
            danger = ClaroDanger,
            barTrack = Color(0x14FFFFFF),
            barFill = ClaroBarFill,
            offline = ClaroDarkWarning,
        )
    } else {
        ClaroColorsExtra(
            textSecondary = ClaroLightTextSecondary,
            border = ClaroLightBorder,
            surfaceAlt = ClaroLightSurfaceAlt,
            warning = ClaroLightWarning,
            danger = ClaroDanger,
            barTrack = Color(0xFFECECEC),
            barFill = ClaroBarFill,
            offline = ClaroLightWarning,
        )
    }

    CompositionLocalProvider(
        LocalClaroColorsExtra provides extra,
        LocalClaroDimens provides ClaroDimens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}