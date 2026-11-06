package com.example.service

import com.example.model.CognitiveAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MnemosyneAiService(
    private val preferences: SettingsPreferences
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun analyzeEvent(
        instantDescription: String,
        imageContextDescription: String,
        eveningDescription: String,
        timestamp: Long = System.currentTimeMillis()
    ): Result<CognitiveAnalysis> = withContext(Dispatchers.IO) {
        val dateStr = SimpleDateFormat("EEEE d MMMM yyyy, HH:mm", Locale.FRENCH).format(Date(timestamp))
        val apiKey = preferences.apiKey.trim()

        // Si aucune clé API DeepSeek n'est configurée, on utilise l'évaluateur cognitif local
        if (apiKey.isBlank()) {
            val localAnalysis = CognitiveEvaluator.evaluate(
                instantDescription = instantDescription,
                imageContextDescription = imageContextDescription,
                eveningDescription = eveningDescription,
                dateTimeStr = dateStr
            )
            return@withContext Result.success(localAnalysis.copy(modelEngine = "Mnémo-Heuristique (Hors-Ligne / Sans Clé)"))
        }

        try {
            val systemPrompt = """
                Tu es "Mnemosyne-AI", un expert en analyse cognitive, en psychologie de la mémoire et en traitement du langage naturel. Ton rôle est de comparer deux versions d'un même événement de la journée d'un utilisateur :
                1. LA VERSION INSTANTANÉE : Capturée au moment exact où l'événement se produit (texte brut + analyse contextuelle de la photo fournie).
                2. LA VERSION NOCTURNE : Rédigée par l'utilisateur de mémoire en fin de journée.

                OBJECTIF :
                - Analyser les convergences, les divergences, les omissions notables et les embellissements (biais de récence, dramatisation, oublis).
                - Calculer un score d'exactitude (en pourcentage de 0 à 100 %) reflétant la fidélité de la mémoire de fin de journée par rapport à la réalité capturée à chaud.

                RÈGLES D'ANALYSE :
                - Sois rigoureux mais bienveillant : distingue un oubli bénin d'une contradiction flagrante.
                - Ne te base pas uniquement sur les mots exacts, mais sur le sens, l'ambiance, les faits et les acteurs impliqués.
                - Fournis une réponse structurée obligatoirement au format JSON strict pour que l'application Android puisse l'exploiter directement, sans texte superflu autour.

                FORMAT DE SORTIE ATTENDU (JSON) :
                {
                  "exactitude_score": 85,
                  "analyse_generale": "Synthèse critique de la fiabilité du souvenir.",
                  "points_communs": [
                    "Élément présent dans les deux versions..."
                  ],
                  "divergences_ou_omissions": [
                    "Détail présent à chaud mais omis ou modifié le soir..."
                  ],
                  "biais_cognitif_detecte": "Ex: Biais de dramatisation, embellissement ou oubli sélectif...",
                  "conseil_reflexif": "Une courte phrase pour inviter l'utilisateur à méditer sur l'écart constaté."
                }
            """.trimIndent()

            val userContent = """
                Voici les données à comparer pour l'événement du $dateStr :

                [DONNÉES INSTANTANÉES - À CHAUD]
                - Description textuelle initiale : "$instantDescription"
                - Contexte visuel / Description de la photo : "$imageContextDescription"

                [DONNÉES NOCTURNES - DE MÉMOIRE]
                - Récit de fin de journée : "$eveningDescription"

                Effectue l'analyse comparative selon les instructions système.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("model", preferences.model.ifBlank { "deepseek-chat" })
                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userContent)
                    })
                }
                put("messages", messagesArray)
                put("response_format", JSONObject().apply {
                    put("type", "json_object")
                })
                put("temperature", 0.4)
            }

            val url = preferences.customUrl.ifBlank { SettingsPreferences.DEFAULT_URL }
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    // Fallback to local cognitive evaluator if API fails (e.g. invalid key or network issue)
                    val fallback = CognitiveEvaluator.evaluate(
                        instantDescription,
                        imageContextDescription,
                        eveningDescription,
                        dateStr
                    )
                    return@withContext Result.success(
                        fallback.copy(
                            analyseGenerale = "${fallback.analyseGenerale}\n\n(Note : Requête API DeepSeek code ${response.code}, analyse réalisée avec le moteur heuristique local).",
                            modelEngine = "Mnémo-Heuristique (Erreur API ${response.code})"
                        )
                    )
                }

                val responseBodyStr = response.body?.string().orEmpty()
                val responseJson = JSONObject(responseBodyStr)
                val choices = responseJson.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    throw IllegalStateException("Réponse DeepSeek invalide (choices manquant)")
                }

                val firstChoice = choices.getJSONObject(0)
                val messageObj = firstChoice.getJSONObject("message")
                val contentStr = messageObj.getString("content")

                // Parser le JSON de l'analyse cognitive
                val analysisJson = JSONObject(contentStr)
                val score = analysisJson.optInt("exactitude_score", 70)
                val analyseGenerale = analysisJson.optString("analyse_generale", "Analyse effectuée.")
                val biaisCognitif = analysisJson.optString("biais_cognitif_detecte", "Biais non spécifié")
                val conseilReflexif = analysisJson.optString("conseil_reflexif", "Méditez sur la plasticité de vos souvenirs.")

                val pointsCommuns = mutableListOf<String>()
                val pointsArray = analysisJson.optJSONArray("points_communs")
                if (pointsArray != null) {
                    for (i in 0 until pointsArray.length()) {
                        pointsCommuns.add(pointsArray.getString(i))
                    }
                }

                val divergences = mutableListOf<String>()
                val divArray = analysisJson.optJSONArray("divergences_ou_omissions")
                if (divArray != null) {
                    for (i in 0 until divArray.length()) {
                        divergences.add(divArray.getString(i))
                    }
                }

                val cognitiveAnalysis = CognitiveAnalysis(
                    exactitudeScore = score,
                    analyseGenerale = analyseGenerale,
                    pointsCommuns = pointsCommuns,
                    divergencesOuOmissions = divergences,
                    biaisCognitifDetecte = biaisCognitif,
                    conseilReflexif = conseilReflexif,
                    analyzedAt = System.currentTimeMillis(),
                    modelEngine = preferences.model.ifBlank { "deepseek-chat" }
                )

                Result.success(cognitiveAnalysis)
            }
        } catch (e: Exception) {
            // Sécurité absolue : fournir toujours une analyse pertinente
            val localAnalysis = CognitiveEvaluator.evaluate(
                instantDescription = instantDescription,
                imageContextDescription = imageContextDescription,
                eveningDescription = eveningDescription,
                dateTimeStr = dateStr
            )
            Result.success(
                localAnalysis.copy(
                    analyseGenerale = "${localAnalysis.analyseGenerale}\n\n(Note : Évaluation par le moteur cognitif local suite à : ${e.message ?: "problème de connexion"}).",
                    modelEngine = "Mnémo-Heuristique (Hors-Ligne)"
                )
            )
        }
    }
}
