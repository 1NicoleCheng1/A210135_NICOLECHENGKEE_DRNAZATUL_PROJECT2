package com.example.a210135_nicolechengkee_drnazatul_project2.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,

    secondary = SecondaryGreen,
    onSecondary = Color.White,

    tertiary = LightGreen,

    background = Background,
    onBackground = Color.Black,

    surface = Surface,
    onSurface = Color.Black
)

@Composable
fun Project2Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}