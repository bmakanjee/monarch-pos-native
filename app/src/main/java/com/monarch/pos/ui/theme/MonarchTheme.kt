package com.monarch.pos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MonarchGold = Color(0xFFD4AF37)
val MonarchBlack = Color(0xFF000000)
val MonarchDarkGray = Color(0xFF1A1A1A)
val MonarchWhite = Color(0xFFFFFFFF)

private val MonarchColors = darkColorScheme(
    primary = MonarchGold,
    onPrimary = MonarchBlack,
    background = MonarchBlack,
    onBackground = MonarchWhite,
    surface = MonarchDarkGray,
    onSurface = MonarchWhite,
    secondary = MonarchGold,
    onSecondary = MonarchBlack,
    error = Color(0xFFCF6679),
    onError = MonarchBlack
)

@Composable
fun MonarchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MonarchColors,
        content = content
    )
}
