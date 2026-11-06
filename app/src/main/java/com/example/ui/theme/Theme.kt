package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Thème Vibrant & Coloré "Spectre Aurore"
private val MnemosyneVibrantDarkColorScheme = darkColorScheme(
    primary = NeonMagenta,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6B0E3B),
    onPrimaryContainer = Color(0xFFFFD9E6),
    secondary = ElectricCyan,
    onSecondary = Color(0xFF003830),
    secondaryContainer = Color(0xFF00564B),
    onSecondaryContainer = Color(0xFFB2FFF2),
    tertiary = SolarGold,
    onTertiary = Color(0xFF3E2C00),
    tertiaryContainer = Color(0xFF5E4300),
    onTertiaryContainer = Color(0xFFFFE08B),
    background = DeepCosmicViolet,
    onBackground = Color(0xFFF7F2FF),
    surface = SurfaceVibrantDark,
    onSurface = Color(0xFFF7F2FF),
    surfaceVariant = SurfaceVariantVibrantDark,
    onSurfaceVariant = Color(0xFFD6C8F5),
    outline = Color(0xFF7A5BAA),
    outlineVariant = Color(0xFF432C68)
)

private val MnemosyneVibrantLightColorScheme = lightColorScheme(
    primary = Color(0xFFD90368),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E6),
    onPrimaryContainer = Color(0xFF40001B),
    secondary = Color(0xFF00A896),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9CF8EA),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = Color(0xFFE08700),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDEAC),
    onTertiaryContainer = Color(0xFF2C1600),
    background = Color(0xFFFAF7FF),
    onBackground = Color(0xFF1B1429),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1429),
    surfaceVariant = Color(0xFFEFE8FC),
    onSurfaceVariant = Color(0xFF4D435E),
    outline = Color(0xFF8B7E9F),
    outlineVariant = Color(0xFFD0C4E4)
)

@Composable
fun MnemosyneTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MnemosyneVibrantDarkColorScheme else MnemosyneVibrantLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
