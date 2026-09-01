package com.bizmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEBCF),
    onPrimaryContainer = Color(0xFF0A320D),
    secondary = Color(0xFF54633F),
    onSecondary = Color.White,
    tertiary = Color(0xFF386663),
    background = Color(0xFFF7FBF4),
    surface = Color(0xFFF7FBF4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8D5AA),
    onPrimary = Color(0xFF0A320D),
    primaryContainer = Color(0xFF0A320D),
    onPrimaryContainer = Color(0xFFA8D5AA),
    secondary = Color(0xFFBAC79F),
    tertiary = Color(0xFF96CAC6),
    background = Color(0xFF101410),
    surface = Color(0xFF101410)
)

@Composable
fun BusinessManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
