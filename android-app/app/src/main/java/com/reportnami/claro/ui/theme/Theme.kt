package com.reportnami.claro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
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

// CompositionLocal for font scale
val LocalFontScale = compositionLocalOf { 1.0f }

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
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

    // Create scaled typography
    val scaledTypography = Typography(
        displayLarge = Typography.displayLarge.copy(fontSize = (Typography.displayLarge.fontSize.value * fontScale).sp),
        displayMedium = Typography.displayMedium.copy(fontSize = (Typography.displayMedium.fontSize.value * fontScale).sp),
        displaySmall = Typography.displaySmall.copy(fontSize = (Typography.displaySmall.fontSize.value * fontScale).sp),
        headlineLarge = Typography.headlineLarge.copy(fontSize = (Typography.headlineLarge.fontSize.value * fontScale).sp),
        headlineMedium = Typography.headlineMedium.copy(fontSize = (Typography.headlineMedium.fontSize.value * fontScale).sp),
        headlineSmall = Typography.headlineSmall.copy(fontSize = (Typography.headlineSmall.fontSize.value * fontScale).sp),
        titleLarge = Typography.titleLarge.copy(fontSize = (Typography.titleLarge.fontSize.value * fontScale).sp),
        titleMedium = Typography.titleMedium.copy(fontSize = (Typography.titleMedium.fontSize.value * fontScale).sp),
        titleSmall = Typography.titleSmall.copy(fontSize = (Typography.titleSmall.fontSize.value * fontScale).sp),
        bodyLarge = Typography.bodyLarge.copy(fontSize = (Typography.bodyLarge.fontSize.value * fontScale).sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = (Typography.bodyMedium.fontSize.value * fontScale).sp),
        bodySmall = Typography.bodySmall.copy(fontSize = (Typography.bodySmall.fontSize.value * fontScale).sp),
        labelLarge = Typography.labelLarge.copy(fontSize = (Typography.labelLarge.fontSize.value * fontScale).sp),
        labelMedium = Typography.labelMedium.copy(fontSize = (Typography.labelMedium.fontSize.value * fontScale).sp),
        labelSmall = Typography.labelSmall.copy(fontSize = (Typography.labelSmall.fontSize.value * fontScale).sp),
    )

    CompositionLocalProvider(
        LocalClaroColorsExtra provides extra,
        LocalClaroDimens provides ClaroDimens(),
        LocalFontScale provides fontScale,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}

// Helper function to scale font sizes
@Composable
fun scaleFontSize(baseSize: androidx.compose.ui.unit.TextUnit): androidx.compose.ui.unit.TextUnit {
    val fontScale = LocalFontScale.current
    return (baseSize.value * fontScale).sp
}