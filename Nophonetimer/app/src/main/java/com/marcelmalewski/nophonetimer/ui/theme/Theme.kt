package com.marcelmalewski.nophonetimer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoPhoneColorScheme = darkColorScheme(
    primary = Color(0xFFFFCC80), secondary = Color(0xFFFFCC80),

    background = Color(0xFF121212), surface = Color(0xFF222222),

    onPrimary = Color.Black, onSecondary = Color.Black,

    onBackground = Color.White, onSurface = Color.White
)

@Composable
fun NoPhoneTimerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoPhoneColorScheme, typography = Typography, content = content
    )
}