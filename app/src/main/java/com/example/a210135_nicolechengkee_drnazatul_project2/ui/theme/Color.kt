package com.example.a210135_nicolechengkee_drnazatul_project2.ui.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

val PrimaryGreen = Color(0xFF346739)
val SecondaryGreen = Color(0xFF79AE6F)
val LightGreen = Color(0xFF9FCB98)
val Background = Color(0xFFF2EDC2)
val Surface = Color(0xFFFFFFFF)

// Material 3 color scheme
private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,

    secondary = SecondaryGreen,
    onSecondary = Color.White,

    tertiary = LightGreen,

    background = Background,
    onBackground = Color.Black,

    surface = Surface,
    onSurface = Color.Black,

    surfaceVariant = Color(0xFFE6E1C5),
    onSurfaceVariant = Color.Gray,

    secondaryContainer = LightGreen
)

