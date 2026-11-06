package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyEvaluation(
    val dateKey: String,          // Format "yyyy-MM-dd"
    val timestamp: Long,          // Représentatif du jour
    val dateLabel: String,         // "Aujourd'hui", "Hier", "3 nov.", etc.
    val dayOfWeek: String,         // "Lun", "Mar", etc.
    val dayOfMonth: String,        // "06", "05", etc.
    val averageScore: Int,         // 0 à 100
    val evaluatedCount: Int,       // Nombre de souvenirs évalués ce jour
    val dominantBias: String,      // Biais le plus fréquent du jour
    val memories: List<EventMemory>, // Liste des souvenirs de ce jour
    val isToday: Boolean
)

data class PerformanceMetrics(
    val globalAverageScore: Int,
    val totalEvaluatedDays: Int,
    val currentStreakDays: Int,
    val bestDay: DailyEvaluation?,
    val lowestDay: DailyEvaluation?,
    val weeklyTrendPercent: Int // Progression en % par rapport aux jours précédents
)

object DailyTrackingHelper {

    private val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.FRENCH)
    private val dayOfMonthFormat = SimpleDateFormat("dd", Locale.FRANCE)
    private val shortDateFormat = SimpleDateFormat("d MMM", Locale.FRENCH)

    fun computeDailyEvaluations(memories: List<EventMemory>): List<DailyEvaluation> {
        val analyzedMemories = memories.filter { it.analysis != null }
        if (analyzedMemories.isEmpty()) return emptyList()

        val todayKey = dayKeyFormat.format(Date())
        val yesterdayKey = dayKeyFormat.format(Date(System.currentTimeMillis() - 86400000L))

        // Grouper les souvenirs par jour (selon la date de l'analyse ou de l'événement)
        val groupedByDay = analyzedMemories.groupBy { memory ->
            val timestamp = memory.analysis?.analyzedAt ?: memory.timestampInstant
            dayKeyFormat.format(Date(timestamp))
        }

        return groupedByDay.map { (dateKey, dayMemories) ->
            val repTimestamp = dayMemories.firstOrNull()?.let {
                it.analysis?.analyzedAt ?: it.timestampInstant
            } ?: System.currentTimeMillis()
            val dateObj = Date(repTimestamp)

            val avgScore = dayMemories.mapNotNull { it.analysis?.exactitudeScore }
                .takeIf { it.isNotEmpty() }
                ?.average()
                ?.toInt() ?: 0

            val dominantBias = dayMemories
                .mapNotNull { it.analysis?.biaisCognitifDetecte }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "Mémoire conservatrice"

            val dateLabel = when (dateKey) {
                todayKey -> "Aujourd'hui"
                yesterdayKey -> "Hier"
                else -> shortDateFormat.format(dateObj)
            }

            DailyEvaluation(
                dateKey = dateKey,
                timestamp = repTimestamp,
                dateLabel = dateLabel,
                dayOfWeek = dayOfWeekFormat.format(dateObj).replace(".", "").replaceFirstChar { it.uppercase() },
                dayOfMonth = dayOfMonthFormat.format(dateObj),
                averageScore = avgScore,
                evaluatedCount = dayMemories.size,
                dominantBias = dominantBias,
                memories = dayMemories,
                isToday = dateKey == todayKey
            )
        }.sortedBy { it.dateKey }
    }

    fun computeMetrics(dailyList: List<DailyEvaluation>): PerformanceMetrics {
        if (dailyList.isEmpty()) {
            return PerformanceMetrics(
                globalAverageScore = 0,
                totalEvaluatedDays = 0,
                currentStreakDays = 0,
                bestDay = null,
                lowestDay = null,
                weeklyTrendPercent = 0
            )
        }

        val globalAvg = dailyList.map { it.averageScore }.average().toInt()
        val bestDay = dailyList.maxByOrNull { it.averageScore }
        val lowestDay = dailyList.minByOrNull { it.averageScore }

        // Calcul de la tendance (différence entre les 2 derniers jours ou 1ère moitié / 2ème moitié)
        val trend = if (dailyList.size >= 2) {
            val last = dailyList.last().averageScore
            val previous = dailyList[dailyList.size - 2].averageScore
            last - previous
        } else {
            0
        }

        // Calcul de streak (jours consécutifs évalués)
        val streak = calculateStreak(dailyList)

        return PerformanceMetrics(
            globalAverageScore = globalAvg,
            totalEvaluatedDays = dailyList.size,
            currentStreakDays = streak,
            bestDay = bestDay,
            lowestDay = lowestDay,
            weeklyTrendPercent = trend
        )
    }

    private fun calculateStreak(dailyList: List<DailyEvaluation>): Int {
        if (dailyList.isEmpty()) return 0
        // Pour un suivi réaliste, compte le nombre de jours consécutifs récents
        var streak = 1
        for (i in dailyList.size - 1 downTo 1) {
            val currentDay = dailyList[i].timestamp
            val prevDay = dailyList[i - 1].timestamp
            val diffDays = (currentDay - prevDay) / 86400000L
            if (diffDays in 1..2) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}
