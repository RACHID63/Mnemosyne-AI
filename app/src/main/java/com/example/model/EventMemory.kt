package com.example.model

import java.util.UUID

enum class MemoryStatus {
    EN_ATTENTE_SOIR, // Capturé à chaud, récit nocturne non rédigé
    PRET_ANALYSE,    // Récit nocturne rédigé, prêt à être analysé par Mnemosyne
    ANALYSE_COMPLETE // Analysé avec score et rapport cognitif
}

data class EventMemory(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val timestampInstant: Long = System.currentTimeMillis(),
    val instantDescription: String,
    val imageContextDescription: String = "",
    val photoUri: String? = null,
    val emotionalStateInstant: String = "Calme",
    val locationInstant: String = "",
    
    // Evening recall
    val eveningDescription: String? = null,
    val timestampEvening: Long? = null,
    val confidenceRating: Int = 3, // 1 to 5
    
    // Cognitive Analysis
    val analysis: CognitiveAnalysis? = null,
    val isFavorite: Boolean = false
) {
    val status: MemoryStatus
        get() = when {
            analysis != null -> MemoryStatus.ANALYSE_COMPLETE
            !eveningDescription.isNullOrBlank() -> MemoryStatus.PRET_ANALYSE
            else -> MemoryStatus.EN_ATTENTE_SOIR
        }
}
