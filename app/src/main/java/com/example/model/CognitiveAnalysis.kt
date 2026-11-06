package com.example.model

data class CognitiveAnalysis(
    val exactitudeScore: Int,
    val analyseGenerale: String,
    val pointsCommuns: List<String>,
    val divergencesOuOmissions: List<String>,
    val biaisCognitifDetecte: String,
    val conseilReflexif: String,
    val analyzedAt: Long = System.currentTimeMillis(),
    val modelEngine: String = "deepseek-chat"
) {
    val fidelityLevel: FidelityLevel
        get() = when {
            exactitudeScore >= 80 -> FidelityLevel.HAUTE
            exactitudeScore >= 50 -> FidelityLevel.MODEREE
            else -> FidelityLevel.DIVERGENTE
        }
}

enum class FidelityLevel(val label: String, val description: String) {
    HAUTE("Excellente fidélité", "Souvenir très proche de la réalité capturée à chaud"),
    MODEREE("Altération modérée", "Quelques divergences notables ou oublis de détails"),
    DIVERGENTE("Divergence marquée", "Reconstruction significative ou biais cognitif prononcé")
}
