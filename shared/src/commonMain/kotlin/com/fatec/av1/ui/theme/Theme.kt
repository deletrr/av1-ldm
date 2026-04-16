package com.fatec.av1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppGreen = Color(0xFF1DB954)
val SpotifyBlack = Color(0xFF121212)
val SpotifyDarkGray = Color(0xFF181818)
val SpotifyGray = Color(0xFF282828)
val SpotifyLightGray = Color(0xFFB3B3B3)
val SpotifyWhite = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = AppGreen,
    onPrimary = SpotifyBlack,
    secondary = SpotifyGray,
    onSecondary = SpotifyWhite,
    background = SpotifyBlack,
    onBackground = SpotifyWhite,
    surface = SpotifyDarkGray,
    onSurface = SpotifyWhite,
    surfaceVariant = SpotifyGray,
    onSurfaceVariant = SpotifyLightGray,
    error = Color(0xFFCF6679),
    outline = Color(0xFF404040),
)

private val LightColorScheme = lightColorScheme(
    primary = AppGreen,
    onPrimary = Color.White,
    secondary = Color(0xFFE0E0E0),
    onSecondary = Color(0xFF1A1A1A),
    background = Color(0xFFD9D9D9),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF555555),
    error = Color(0xFFB00020),
    outline = Color(0xFFCCCCCC),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography(), content = content)
}
