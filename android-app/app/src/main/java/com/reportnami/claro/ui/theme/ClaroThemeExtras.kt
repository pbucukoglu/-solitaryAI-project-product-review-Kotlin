package com.reportnami.claro.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ClaroColorsExtra(
    val textSecondary: Color,
    val border: Color,
    val surfaceAlt: Color,
    val warning: Color,
    val danger: Color,
    val barTrack: Color,
    val barFill: Color,
    val offline: Color,
)

@Immutable
data class ClaroDimens(
    val xs: Int = 6,
    val sm: Int = 10,
    val md: Int = 16,
    val lg: Int = 20,
    val xl: Int = 28,
    val radiusSm: Int = 8,
    val radiusMd: Int = 12,
    val radiusLg: Int = 16,
    val radiusCard: Int = 18,
)

val LocalClaroColorsExtra = staticCompositionLocalOf {
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

val LocalClaroDimens = staticCompositionLocalOf { ClaroDimens() }

object ClaroTheme {
    val colorsExtra: ClaroColorsExtra
        @Composable get() = LocalClaroColorsExtra.current

    val dimens: ClaroDimens
        @Composable get() = LocalClaroDimens.current
}
