package com.example.data

import android.content.Context
import com.example.model.CognitiveAnalysis
import com.example.model.EventMemory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MemoryRepository(private val context: Context) {

    private val storageFile = File(context.filesDir, "mnemosyne_memories.json")
    private val _memories = MutableStateFlow<List<EventMemory>>(emptyList())
    val memories: StateFlow<List<EventMemory>> = _memories.asStateFlow()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        if (storageFile.exists()) {
            try {
                val jsonString = storageFile.readText()
                val list = deserializeMemories(jsonString)
                if (list.isNotEmpty()) {
                    _memories.value = list
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // First launch or empty: populate with rich default memories demonstrating the cognitive science
        val samples = createSampleMemories()
        _memories.value = samples
        persistMemories(samples)
    }

    private fun persistMemories(list: List<EventMemory>) {
        try {
            val jsonArray = serializeMemories(list)
            storageFile.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addInstantMemory(
        title: String,
        instantDescription: String,
        imageContextDescription: String,
        photoUri: String?,
        emotionalState: String,
        location: String
    ): EventMemory {
        val newMemory = EventMemory(
            title = title,
            timestampInstant = System.currentTimeMillis(),
            instantDescription = instantDescription,
            imageContextDescription = imageContextDescription,
            photoUri = photoUri,
            emotionalStateInstant = emotionalState,
            locationInstant = location
        )
        val updated = listOf(newMemory) + _memories.value
        _memories.value = updated
        persistMemories(updated)
        return newMemory
    }

    fun updateEveningRecall(
        id: String,
        eveningDescription: String,
        confidenceRating: Int
    ) {
        val updated = _memories.value.map { memory ->
            if (memory.id == id) {
                memory.copy(
                    eveningDescription = eveningDescription,
                    timestampEvening = System.currentTimeMillis(),
                    confidenceRating = confidenceRating
                )
            } else {
                memory
            }
        }
        _memories.value = updated
        persistMemories(updated)
    }

    fun saveAnalysis(id: String, analysis: CognitiveAnalysis) {
        val updated = _memories.value.map { memory ->
            if (memory.id == id) {
                memory.copy(analysis = analysis)
            } else {
                memory
            }
        }
        _memories.value = updated
        persistMemories(updated)
    }

    fun deleteMemory(id: String) {
        val updated = _memories.value.filter { it.id != id }
        _memories.value = updated
        persistMemories(updated)
    }

    fun toggleFavorite(id: String) {
        val updated = _memories.value.map { memory ->
            if (memory.id == id) {
                memory.copy(isFavorite = !memory.isFavorite)
            } else {
                memory
            }
        }
        _memories.value = updated
        persistMemories(updated)
    }

    fun resetToSamples() {
        val samples = createSampleMemories()
        _memories.value = samples
        persistMemories(samples)
    }

    private fun serializeMemories(list: List<EventMemory>): JSONArray {
        val array = JSONArray()
        for (m in list) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("title", m.title)
                put("timestampInstant", m.timestampInstant)
                put("instantDescription", m.instantDescription)
                put("imageContextDescription", m.imageContextDescription)
                put("photoUri", m.photoUri ?: "")
                put("emotionalStateInstant", m.emotionalStateInstant)
                put("locationInstant", m.locationInstant)
                put("eveningDescription", m.eveningDescription ?: "")
                put("timestampEvening", m.timestampEvening ?: 0L)
                put("confidenceRating", m.confidenceRating)
                put("isFavorite", m.isFavorite)

                if (m.analysis != null) {
                    val a = m.analysis
                    val aObj = JSONObject().apply {
                        put("exactitudeScore", a.exactitudeScore)
                        put("analyseGenerale", a.analyseGenerale)
                        put("biaisCognitifDetecte", a.biaisCognitifDetecte)
                        put("conseilReflexif", a.conseilReflexif)
                        put("analyzedAt", a.analyzedAt)
                        put("modelEngine", a.modelEngine)
                        put("pointsCommuns", JSONArray(a.pointsCommuns))
                        put("divergencesOuOmissions", JSONArray(a.divergencesOuOmissions))
                    }
                    put("analysis", aObj)
                }
            }
            array.put(obj)
        }
        return array
    }

    private fun deserializeMemories(jsonStr: String): List<EventMemory> {
        val result = mutableListOf<EventMemory>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val analysisObj = obj.optJSONObject("analysis")
            var analysis: CognitiveAnalysis? = null
            if (analysisObj != null) {
                val pts = mutableListOf<String>()
                val ptsArr = analysisObj.optJSONArray("pointsCommuns")
                if (ptsArr != null) {
                    for (j in 0 until ptsArr.length()) pts.add(ptsArr.getString(j))
                }
                val divs = mutableListOf<String>()
                val divArr = analysisObj.optJSONArray("divergencesOuOmissions")
                if (divArr != null) {
                    for (j in 0 until divArr.length()) divs.add(divArr.getString(j))
                }
                analysis = CognitiveAnalysis(
                    exactitudeScore = analysisObj.optInt("exactitudeScore", 75),
                    analyseGenerale = analysisObj.optString("analyseGenerale", ""),
                    pointsCommuns = pts,
                    divergencesOuOmissions = divs,
                    biaisCognitifDetecte = analysisObj.optString("biaisCognitifDetecte", ""),
                    conseilReflexif = analysisObj.optString("conseilReflexif", ""),
                    analyzedAt = analysisObj.optLong("analyzedAt", System.currentTimeMillis()),
                    modelEngine = analysisObj.optString("modelEngine", "deepseek-chat")
                )
            }

            val memory = EventMemory(
                id = obj.getString("id"),
                title = obj.getString("title"),
                timestampInstant = obj.optLong("timestampInstant", System.currentTimeMillis()),
                instantDescription = obj.getString("instantDescription"),
                imageContextDescription = obj.optString("imageContextDescription", ""),
                photoUri = obj.optString("photoUri").takeIf { it.isNotBlank() },
                emotionalStateInstant = obj.optString("emotionalStateInstant", "Calme"),
                locationInstant = obj.optString("locationInstant", ""),
                eveningDescription = obj.optString("eveningDescription").takeIf { it.isNotBlank() },
                timestampEvening = obj.optLong("timestampEvening").takeIf { it > 0 },
                confidenceRating = obj.optInt("confidenceRating", 3),
                analysis = analysis,
                isFavorite = obj.optBoolean("isFavorite", false)
            )
            result.add(memory)
        }
        return result
    }

    private fun createSampleMemories(): List<EventMemory> {
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 3600 * 1000 * 3
        val yesterday = now - 3600 * 1000 * 26

        val sample1 = EventMemory(
            title = "Réunion impromptue projet Alpha",
            timestampInstant = oneHourAgo,
            instantDescription = "Marc est arrivé avec 12 minutes de retard avec un café noir. Il a proposé de décaler la release au 18 novembre. Sophie n'était pas d'accord et a froncé les sourcils mais a accepté de réexaminer le sprint.",
            imageContextDescription = "Salle de réunion B12, tableau blanc avec diagramme d'architecture bleu, tasse en carton renversée à moitié sur la table en hêtre clair.",
            emotionalStateInstant = "Stressé",
            locationInstant = "Salle B12 - Siège",
            eveningDescription = "On a eu une réunion imprévue cet après-midi. Marc nous a annoncé qu'il fallait décaler tout le projet d'un mois car rien n'était prêt. Sophie était furieuse et a refusé net, l'ambiance était électrique.",
            timestampEvening = now - 3600 * 1000,
            confidenceRating = 4,
            analysis = CognitiveAnalysis(
                exactitudeScore = 48,
                analyseGenerale = "Divergence marquée causée par un biais de dramatisation rétrospectif. Le retard de 12 minutes et le décalage de quelques jours ont été amplifiés en 'rien n'était prêt' et 'décalage d'un mois'. L'attitude réservée de Sophie a été réécrite en fureur hostile.",
                pointsCommuns = listOf(
                    "Présence des acteurs clés (Marc et Sophie) confirmée.",
                    "Thématique centrale exacte : discussion sur le report du calendrier de livraison.",
                    "Tension perçue entre les participants."
                ),
                divergencesOuOmissions = listOf(
                    "Dramatisation factuelle : décalage réel de quelques jours transformé en décalage d'un mois.",
                    "Exagération émotionnelle : Sophie a froncé les sourcils puis accepté, alors que le souvenir nocturne affirme qu'elle était 'furieuse et a refusé net'.",
                    "Omission contextuelle : la tasse en carton et les détails concrets du tableau blanc ont complètement disparu du souvenir."
                ),
                biaisCognitifDetecte = "Biais de dramatisation et amplification émotionnelle rétrospective",
                conseilReflexif = "La fatigue du soir transforme souvent une négociation tendue mais polie en affrontement hostile. Vérifiez vos notes écrites avant de réagir à chaud.",
                analyzedAt = now - 1800 * 1000,
                modelEngine = "deepseek-chat"
            )
        )

        val sample2 = EventMemory(
            title = "Déjeuner en terrasse au Petit Marais",
            timestampInstant = yesterday,
            instantDescription = "Déjeuner avec Clara. J'ai pris les gnocchis à la truffe et elle une salade burrata. Le serveur avait un tablier rayé vert et nous a offert deux mini-financiers avec l'addition.",
            imageContextDescription = "Terrasse ensoleillée, nappe vichy rouge et blanche, carafe d'eau embuée avec rondelle de citron, deux cafés allongés.",
            emotionalStateInstant = "Joyeux",
            locationInstant = "Le Petit Marais, Paris",
            eveningDescription = "Super moment à midi avec Clara au resto italien. On a partagé de délicieuses pâtes et une salade. Le soleil tapait fort sur la terrasse, et le serveur était adorable, il nous a même offert des petits gâteaux avec l'addition !",
            timestampEvening = yesterday + 3600 * 1000 * 7,
            confidenceRating = 5,
            analysis = CognitiveAnalysis(
                exactitudeScore = 88,
                analyseGenerale = "Excellente fidélité mémorielle. Les plats commandés, les gestes de courtoisie du serveur et l'atmosphère ensoleillée sont fidèlement gravés dans votre mémoire avec une concordance remarquable.",
                pointsCommuns = listOf(
                    "Exactitude des plats commandés (pâtes/gnocchis et salade avec Clara).",
                    "Rappel précis de l'attention du serveur (mini-financiers offerts).",
                    "Cohérence émotionnelle parfaite : plaisir et détente partagés."
                ),
                divergencesOuOmissions = listOf(
                    "Omission bénigne du détail vestimentaire (le tablier rayé vert du serveur).",
                    "La mémoire a synthétisé 'gnocchis à la truffe' en 'délicieuses pâtes'."
                ),
                biaisCognitifDetecte = "Mémoire épisodique hautement conservatrice avec légère condensation sémantique",
                conseilReflexif = "Votre mémoire fonctionne à son zénith quand l'attention est soutenue et l'émotion positive et apaisée.",
                analyzedAt = yesterday + 3600 * 1000 * 8,
                modelEngine = "deepseek-chat"
            )
        )

        val sample3 = EventMemory(
            title = "Rencontre fortuite à la librairie",
            timestampInstant = now - 3600 * 1000 * 5,
            instantDescription = "Croisé Julien au rayon philosophie. Il feuilletait un essai de Jankélévitch sur le pardon. Il portait un imperméable beige mouillé par la bruine et avait un sac à dos en cuir cognac.",
            imageContextDescription = "Librairie ancienne, étagères en chêne sombre, parquet qui grince, odeur de papier neuf et café en arrière-plan.",
            emotionalStateInstant = "Surpris",
            locationInstant = "Librairie Compagnie",
            eveningDescription = null, // En attente de rédaction ce soir !
            confidenceRating = 3,
            analysis = null
        )

        val twoDaysAgo = now - 86400000L * 2
        val sample4 = EventMemory(
            title = "Briefing design et refonte UI",
            timestampInstant = twoDaysAgo,
            instantDescription = "Présentation de 4 maquettes Figma avec le lead designer. 2 écrans ont été validés, un rejeté à cause du contraste, et un mis en attente pour retravail des icônes.",
            imageContextDescription = "Écran 27 pouces branché en HDMI, post-its roses et jaunes collés sur le cadre.",
            emotionalStateInstant = "Concentré",
            locationInstant = "Open space Design",
            eveningDescription = "On a passé en revue les maquettes. Presque tout a été validé sauf un écran avec un souci de couleur. On a collé des post-its partout pour les corrections.",
            timestampEvening = twoDaysAgo + 3600 * 1000 * 6,
            confidenceRating = 4,
            analysis = CognitiveAnalysis(
                exactitudeScore = 78,
                analyseGenerale = "Bonne concordance globale. L'essentiel de l'arbitrage est retenu, avec une légère simplification sur le sort du quatrième écran (mis en attente).",
                pointsCommuns = listOf(
                    "Validation partielle des maquettes confirmée.",
                    "Souci de contraste/couleur fidèlement mémorisé.",
                    "Détail des post-its présent dans les deux récits."
                ),
                divergencesOuOmissions = listOf(
                    "Omission du détail : un écran était rejeté et un autre simplement en attente."
                ),
                biaisCognitifDetecte = "Oubli sélectif de nuances de statut",
                conseilReflexif = "La mémoire privilégie la catégorisation binaire (validé / non validé) plutôt que les statuts intermédiaires.",
                analyzedAt = twoDaysAgo + 3600 * 1000 * 7,
                modelEngine = "deepseek-chat"
            )
        )

        val threeDaysAgo = now - 86400000L * 3
        val sample5 = EventMemory(
            title = "Course au parc des Buttes-Chaumont",
            timestampInstant = threeDaysAgo,
            instantDescription = "Footing de 45 minutes sous un ciel voilé. Température de 14 degrés. Croisé deux border collies près du pont suspendu et un musicien qui jouait de la flûte traversière.",
            imageContextDescription = "Pont métallique suspendu vert, feuilles dorées d'automne au sol, lac en contrebas.",
            emotionalStateInstant = "Serein",
            locationInstant = "Parc des Buttes-Chaumont",
            eveningDescription = "Super footing de 45 min ce matin aux Buttes-Chaumont. Il faisait frais et calme. Je me souviens particulièrement d'un joueur de flûte près du pont suspendu et des chiens qui couraient dans les feuilles.",
            timestampEvening = threeDaysAgo + 3600 * 1000 * 8,
            confidenceRating = 5,
            analysis = CognitiveAnalysis(
                exactitudeScore = 94,
                analyseGenerale = "Fidélité exceptionnelle. Les éléments clés, l'ambiance sonore (flûte), les animaux et la durée sont restitués avec une rare précision épisodique.",
                pointsCommuns = listOf(
                    "Durée de course exacte (45 minutes).",
                    "Présence du joueur de flûte au pont suspendu remarquablement conservée.",
                    "Atmosphère d'automne et présence canine vérifiées."
                ),
                divergencesOuOmissions = listOf(
                    "Légère omission de la race exacte des chiens (border collies)."
                ),
                biaisCognitifDetecte = "Mémoire épisodique hautement conservatrice",
                conseilReflexif = "L'activité physique aérobie et l'état serein favorisent un encodage mnésique optimal dans l'hippocampe.",
                analyzedAt = threeDaysAgo + 3600 * 1000 * 9,
                modelEngine = "deepseek-chat"
            )
        )

        val fourDaysAgo = now - 86400000L * 4
        val sample6 = EventMemory(
            title = "Visite d'atelier d'ébénisterie",
            timestampInstant = fourDaysAgo,
            instantDescription = "Rencontre avec Thomas l'ébéniste. Atelier encombré et poussiéreux, odeur de sciure de noyer. Devis estimé à 780 euros pour la restauration du buffet, payable en deux fois.",
            imageContextDescription = "Rabots anciens alignés, copeaux de bois blonds sur l'établi, buffet en chêne décapé à moitié.",
            emotionalStateInstant = "Curieux",
            locationInstant = "Atelier rue Faidherbe",
            eveningDescription = "Visite de l'ébéniste aujourd'hui. L'atelier était magnifique, une vraie caverne d'Ali Baba pleine d'histoire. Il m'a fait un super prix autour de 600 ou 700 euros pour restaurer tout le meuble.",
            timestampEvening = fourDaysAgo + 3600 * 1000 * 7,
            confidenceRating = 3,
            analysis = CognitiveAnalysis(
                exactitudeScore = 62,
                analyseGenerale = "Biais d'embellissement rétrospectif notable. L'atelier poussiéreux est devenu 'magnifique et magique', et le devis exact (780€) a été sous-estimé dans le souvenir du soir.",
                pointsCommuns = listOf(
                    "Visite de l'ébéniste confirmée.",
                    "Sensibilité à l'ambiance du bois et des outils anciens."
                ),
                divergencesOuOmissions = listOf(
                    "Sous-estimation du prix : 780€ transformé en 'autour de 600 ou 700€'.",
                    "Idéalisation de l'environnement : atelier encombré embelli en 'caverne d'Ali Baba'."
                ),
                biaisCognitifDetecte = "Biais d'embellissement rétrospectif (Rosy Retrospection)",
                conseilReflexif = "Attention aux chiffres et aux devis : notre cerveau a tendance à retenir les montants qui l'arrangent le plus.",
                analyzedAt = fourDaysAgo + 3600 * 1000 * 8,
                modelEngine = "deepseek-chat"
            )
        )

        return listOf(sample1, sample2, sample3, sample4, sample5, sample6)
    }
}
