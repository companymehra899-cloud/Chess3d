package com.onlinechessgame.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChessDarkColorScheme = darkColorScheme(
    primary = ChessCyan,
    onPrimary = Color.Black,
    secondary = ChessBlue,
    onSecondary = Color.White,
    tertiary = ChessGold,
    background = ChessDarkBg,
    onBackground = Color.White,
    surface = ChessDarkSurface,
    onSurface = Color.White,
    surfaceVariant = ChessCardBg,
    onSurfaceVariant = Color(0xFFCBD5E1)
)

@Composable
fun Chess3DTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChessDarkColorScheme,
        typography = Typography,
        content = content
    )
}

