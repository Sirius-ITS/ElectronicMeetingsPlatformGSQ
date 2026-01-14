package com.informatique.electronicmeetingsplatform.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val background: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val accent: Color,
    val maroonColor: Color,
    val blueColor: Color,
    val lightDrab: Color,
    val textGray: Color
)
val LightExtraColors = ExtraColors(
    background = BackgroundLight,
    success = SuccessLight,
    warning = WarningLight,
    error = ErrorLight,
    accent = AccentLight,
    maroonColor = MaroonColor,
    blueColor = BlueColor,
    lightDrab = LightDrabColor,
    textGray = TextGray
)

val DarkExtraColors = ExtraColors(
    background = BackgroundDark,
    success = SuccessDark,
    warning = WarningLight,
    error = ErrorLight,
    accent = AccentDark,
    maroonColor = MaroonColor,
    blueColor = BlueColor,
    lightDrab = LightDrabColor,
    textGray = TextGray
)

val LocalExtraColors = staticCompositionLocalOf { LightExtraColors }