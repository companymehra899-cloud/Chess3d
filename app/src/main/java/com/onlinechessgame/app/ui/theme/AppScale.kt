package com.onlinechessgame.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppType {
    val micro: TextUnit = 12.sp
    val caption: TextUnit = 13.sp
    val label: TextUnit = 14.sp
    val body: TextUnit = 16.sp
    val title: TextUnit = 18.sp
    val headline: TextUnit = 22.sp
    val display: TextUnit = 28.sp
}

object AppSpace {
    val xs: Dp = 6.dp
    val sm: Dp = 10.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val card: Dp = 16.dp
    val screen: Dp = 16.dp
    val boardMax: Dp = 560.dp
}

@Composable
fun ProvideReadableDensity(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val clamped = Density(
        density = density.density,
        fontScale = density.fontScale.coerceIn(1.0f, 1.12f)
    )
    CompositionLocalProvider(LocalDensity provides clamped, content = content)
}
