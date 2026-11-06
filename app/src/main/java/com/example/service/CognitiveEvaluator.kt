package com.example.service

import com.example.model.CognitiveAnalysis
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Moteur d'évaluation cognitive heuristique Mnemosyne-AI.
 * Analyse les traces sémantiques, contextuelles et émotionnelles
 * pour comparer la capture à chaud et le souvenir de fin de journée.
 */
object CognitiveEvaluator {

    fun evaluate(
        instantDescription: String,
        imageContextDescription: String,
        eveningDescription: String,
        dateTimeStr: String = "Aujourd'hui"
    ): CognitiveAnalysis {
        val instantCombined = "$instantDescription $imageContextDescription".trim().lowercase(Locale.FRENCH)
        val eveningText = eveningDescription.trim().lowercase(Locale.FRENCH)

        val instantWords = extractSignificantWords(instantCombined)
        val eveningWords = extractSignificantWords(eveningText)

        // Convergence calculée sur les concepts clés
        val commonWords = instantWords.intersect(eveningWords)
        val unionWords = instantWords.union(eveningWords)
        val jaccardScore = if (unionWords.isNotEmpty()) {
            (commonWords.size.toDouble() / unionWords.size.toDouble())
        } else {
            0.5
        }

        // Détection de biais cognitif par marqueurs sémantiques
        val dramatizationMarkers = listOf(
            "catastrophe", "terrible", "panique", "horrible", "désastre", "furieux",
            "enfer", "crié", "jamais", "toujours", "chaos", "scandale", "insupportable"
        )
        val embellishmentMarkers = listOf(
            "merveilleux", "parfait", "incroyable", "magique", "fantastique",
            "génial", "succès total", "sublime", "tout le monde adorait", "sans faute"
        )
        val doubtMarkers = listOf(
            "je crois", "peut-être", "il me semble", "sauf erreur", "flou",
            "je ne sais plus", "probablement", "environ"
        )

        val hasDramatization = dramatizationMarkers.any { eveningText.contains(it) } &&
                !dramatizationMarkers.any { instantCombined.contains(it) }

        val hasEmbellishment = embellishmentMarkers.any { eveningText.contains(it) } &&
                !embellishmentMarkers.any { instantCombined.contains(it) }

        val hasHighDoubt = doubtMarkers.count { eveningText.contains(it) } >= 2

        // Calcul du score d'exactitude (0 à 100)
        var scoreRaw = (jaccardScore * 75.0) + 25.0
        
        // Ajustements selon la longueur et la complétude
        val lengthRatio = eveningText.length.toDouble() / max(instantCombined.length.toDouble(), 1.0)
        if (lengthRatio < 0.4) {
            scoreRaw -= 15.0 // Oubli massif
        } else if (lengthRatio > 2.0 && !instantCombined.contains("détail")) {
            scoreRaw -= 10.0 // Confabulation / ajout de détails a posteriori
        }

        if (hasDramatization) scoreRaw -= 12.0
        if (hasEmbellishment) scoreRaw -= 8.0

        val finalScore = max(20, min(98, scoreRaw.roundToInt()))

        // Identification du biais cognitif
        val biais = when {
            hasDramatization -> "Biais de dramatisation (surévaluation de la négativité de l'événement)"
            hasEmbellishment -> "Biais d'embellissement rétrospectif (Rosy Retrospection)"
            hasHighDoubt -> "Affaiblissement de la trace mnésique avec métacognition lucide"
            lengthRatio < 0.45 -> "Biais d'omission sélective et compression mémorielle"
            lengthRatio > 1.8 -> "Effet de reconstruction fabulatoire (complétion inconsciente)"
            finalScore >= 80 -> "Mémoire épisodique hautement conservatrice"
            else -> "Biais de récence avec distorsion contextuelle modérée"
        }

        // Extraction de points communs intelligibles
        val pointsCommuns = mutableListOf<String>()
        if (commonWords.isNotEmpty()) {
            val keyElements = commonWords.take(4).joinToString(", ") { "'$it'" }
            pointsCommuns.add("Accord factuel solide sur les thèmes centraux : $keyElements.")
        } else {
            pointsCommuns.add("Continuité globale de la trame narrative de l'événement.")
        }
        if (instantDescription.split(" ").take(3).any { eveningDescription.contains(it, ignoreCase = true) }) {
            pointsCommuns.add("L'intention première et l'action principale sont fidèlement restituées.")
        }
        pointsCommuns.add("La temporalité et l'ambiance générale correspondent au moment vécu.")

        // Divergences ou omissions
        val divergences = mutableListOf<String>()
        val missedInstantWords = (instantWords - eveningWords).take(3)
        if (missedInstantWords.isNotEmpty()) {
            divergences.add("Omission de détails contextuels présents à chaud : ${missedInstantWords.joinToString(", ") { "'$it'" }}.")
        }
        if (imageContextDescription.isNotBlank() && !imageContextDescription.split(" ").take(4).any { eveningText.contains(it) }) {
            divergences.add("Les indices visuels de la scène ($imageContextDescription) ont été largement filtrés par votre mémoire.")
        }
        if (hasDramatization) {
            divergences.add("La charge émotionnelle nocturne est nettement plus exacerbée qu'au moment précis de l'action.")
        }
        if (hasEmbellishment) {
            divergences.add("Les aspérités et petites tensions notées à chaud ont été adoucies dans votre récit du soir.")
        }
        if (divergences.isEmpty()) {
            divergences.add("Légère altération sur les nuances temporelles précises et l'ordre des micro-actions.")
        }

        // Synthèse générale
        val analyseGenerale = when {
            finalScore >= 80 -> "Excellente fidélité mémorielle. Votre récit nocturne préserve fidèlement les faits bruts, les acteurs et le contexte émotionnel initial sans réécriture trompeuse."
            finalScore >= 60 -> "Fidélité globale convenable mais votre mémoire a commencé son travail de réorganisation : plusieurs détails sensoriels ont disparu au profit d'une synthèse condensée."
            else -> "Écart significatif constaté entre le moment vécu et sa reconstruction tardive. Votre cerveau a comblé les vides ou dramatisé/idéalisé l'expérience sous le prisme de la fatigue ou du recul."
        }

        // Conseil réflexif
        val conseilReflexif = when {
            hasDramatization -> "Demandez-vous si ce qui vous paraissait frustrant ce soir n'était pas en réalité une simple péripétie sans gravité sur le vif."
            hasEmbellishment -> "La mémoire aime peindre le passé en or pour protéger notre bien-être. Savourez ce biais positif tout en restant conscient de la réalité brute."
            lengthRatio < 0.5 -> "Les détails visuels s'évaporent rapidement. Prenez le temps de fixer mentalement les couleurs et les visages au moment où ils se produisent."
            finalScore >= 80 -> "Votre acuité mémorielle est remarquable aujourd'hui. Notez la clarté d'esprit qui vous a permis une telle fidélité cognitive."
            else -> "Observez comment le cerveau préfère créer une histoire cohérente plutôt que de retenir une archive brute. C'est l'essence même de l'esprit humain."
        }

        return CognitiveAnalysis(
            exactitudeScore = finalScore,
            analyseGenerale = analyseGenerale,
            pointsCommuns = pointsCommuns,
            divergencesOuOmissions = divergences,
            biaisCognitifDetecte = biais,
            conseilReflexif = conseilReflexif,
            modelEngine = "Mnemosyne Cognitive Engine"
        )
    }

    private fun extractSignificantWords(text: String): Set<String> {
        val stopWords = setOf(
            "le", "la", "les", "un", "une", "des", "du", "de", "d", "l", "et", "ou",
            "mais", "donc", "or", "ni", "car", "que", "qui", "quoi", "dont", "où",
            "en", "dans", "sur", "sous", "par", "pour", "avec", "sans", "ce", "cet",
            "cette", "ces", "mon", "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses",
            "notre", "nos", "votre", "vos", "leur", "leurs", "je", "tu", "il", "elle",
            "on", "nous", "vous", "ils", "elles", "suis", "es", "est", "sommes", "êtes",
            "sont", "ai", "as", "a", "avons", "avez", "ont", "été", "avoir", "être",
            "fait", "faire", "très", "plus", "moins", "bien", "mal", "aussi", "tout"
        )
        return text.split(Regex("[^\\p{L}\\p{Nd}]+"))
            .filter { it.length > 2 && it !in stopWords }
            .toSet()
    }
}
