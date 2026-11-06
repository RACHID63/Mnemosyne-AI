package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Palette Ultra-Colorée & Vibrante "Spectre Synaptique / Aurore Boréale"
val DeepCosmicViolet = Color(0xFF0F0826)
val SurfaceVibrantDark = Color(0xFF1B113B)
val SurfaceVariantVibrantDark = Color(0xFF281A54)
val SurfaceElevatedVibrant = Color(0xFF34236B)

// Accents Néon & Chromatiques Électriques
val NeonMagenta = Color(0xFFFF2A85)
val ElectricCyan = Color(0xFF00F5D4)
val SolarGold = Color(0xFFFFB703)
val VividViolet = Color(0xFF9D4EDD)
val AuroraTeal = Color(0xFF06D6A0)
val CyberCoral = Color(0xFFFF4D6D)
val ElectricBlue = Color(0xFF4361EE)
val RadiantAmber = Color(0xFFFB8500)

// Semantic Theme Tokens
val NeuroViolet = Color(0xFFB58EFF)
val NeuroVioletContainer = Color(0xFF4C2A96)
val OnNeuroViolet = Color(0xFF1A004C)
val OnNeuroVioletContainer = Color(0xFFF1E9FF)

val SynapticCyan = Color(0xFF00F5D4)
val SynapticCyanContainer = Color(0xFF00564B)
val OnSynapticCyan = Color(0xFF003830)
val OnSynapticCyanContainer = Color(0xFFB2FFF2)

val MemoryGold = Color(0xFFFFB703)
val MemoryGoldContainer = Color(0xFF5F4100)
val OnMemoryGold = Color(0xFF3D2A00)
val OnMemoryGoldContainer = Color(0xFFFFE7A3)

val FidelityHighGreen = Color(0xFF10B981)
val FidelityMediumAmber = Color(0xFFF59E0B)
val FidelityLowRed = Color(0xFFFF3366)

// Gradients prédéfinis pour une esthétique ultra-colorée
object MnemosyneGradients {
    val Aurora = Brush.horizontalGradient(
        colors = listOf(NeonMagenta, VividViolet, ElectricCyan)
    )
    val SunsetVibrant = Brush.horizontalGradient(
        colors = listOf(CyberCoral, RadiantAmber, SolarGold)
    )
    val SynapticNeon = Brush.horizontalGradient(
        colors = listOf(ElectricCyan, AuroraTeal, SolarGold)
    )
    val CosmicCard = Brush.verticalGradient(
        colors = listOf(SurfaceVariantVibrantDark, SurfaceVibrantDark)
    )
    val HeroMesh = Brush.radialGradient(
        colors = listOf(
            VividViolet.copy(alpha = 0.45f),
            NeonMagenta.copy(alpha = 0.25f),
            DeepCosmicViolet
        ),
        radius = 1200f
    )
}

// Couleurs par émotion pour un affichage coloré et vivant
fun getEmotionColor(emotion: String): Color = when (emotion.lowercase()) {
    "joyeux" -> SolarGold
    "calme", "détendu" -> AuroraTeal
    "émerveillé" -> NeonMagenta
    "concentré" -> ElectricCyan
    "stressé", "inquiet" -> CyberCoral
    "surpris" -> VividViolet
    "fatigué" -> ElectricBlue
    "frustré", "en colère" -> Color(0xFFFF3366)
    else -> SynapticCyan
}
