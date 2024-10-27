package com.example.todolist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema Claro
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),
    primaryContainer = Color(0xB300F354),
    secondary = Color(0xFF03DAC5),
    background = Color(0x69FFFFFF), // Gris claro en lugar de blanco puro
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White
)

// Tema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    primaryContainer = Color(0xFF303F9F), // Azul profundo
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF1C1C1E), // Gris oscuro
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun ToDoListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
