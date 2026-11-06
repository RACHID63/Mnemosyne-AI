package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MemoryRepository
import com.example.model.CognitiveAnalysis
import com.example.model.DailyEvaluation
import com.example.model.DailyTrackingHelper
import com.example.model.EventMemory
import com.example.model.MemoryStatus
import com.example.model.PerformanceMetrics
import com.example.service.MnemosyneAiService
import com.example.service.SettingsPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MemoryFilter(val label: String) {
    TOUS("Tous"),
    A_COMPLETER("Ce soir (À rédiger)"),
    ANALYSES("Analysés"),
    FAVORIS("Favoris")
}

enum class NavigationTab(val label: String) {
    JOURNAL("Journal"),
    CAPTURE("À chaud"),
    NOCTURNE("De mémoire"),
    INSIGHTS("Sciences & Suivi")
}

data class UiState(
    val currentTab: NavigationTab = NavigationTab.JOURNAL,
    val selectedFilter: MemoryFilter = MemoryFilter.TOUS,
    val selectedMemory: EventMemory? = null,
    val selectedDailyEvaluation: DailyEvaluation? = null,
    val isAnalyzing: Boolean = false,
    val analyzingMemoryId: String? = null,
    val analysisError: String? = null,
    val showSettingsDialog: Boolean = false,
    val showUserGuideDialog: Boolean = false,
    val eveningTargetMemory: EventMemory? = null
)

class MnemosyneViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = SettingsPreferences(application)
    private val repository = MemoryRepository(application)
    private val aiService = MnemosyneAiService(preferences)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    val allMemories: StateFlow<List<EventMemory>> = repository.memories

    val filteredMemories: StateFlow<List<EventMemory>> = combine(
        allMemories,
        _uiState
    ) { memories, state ->
        when (state.selectedFilter) {
            MemoryFilter.TOUS -> memories
            MemoryFilter.A_COMPLETER -> memories.filter { it.status == MemoryStatus.EN_ATTENTE_SOIR }
            MemoryFilter.ANALYSES -> memories.filter { it.status == MemoryStatus.ANALYSE_COMPLETE }
            MemoryFilter.FAVORIS -> memories.filter { it.isFavorite }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> = combine(allMemories) { list ->
        list[0].count { it.status == MemoryStatus.EN_ATTENTE_SOIR }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageAccuracy: StateFlow<Int> = combine(allMemories) { list ->
        val analyzed = list[0].mapNotNull { it.analysis }
        if (analyzed.isEmpty()) 0 else (analyzed.sumOf { it.exactitudeScore } / analyzed.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dailyEvaluations: StateFlow<List<DailyEvaluation>> = combine(allMemories) { list ->
        DailyTrackingHelper.computeDailyEvaluations(list[0])
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val performanceMetrics: StateFlow<PerformanceMetrics> = combine(dailyEvaluations) { list ->
        DailyTrackingHelper.computeMetrics(list[0])
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PerformanceMetrics(0, 0, 0, null, null, 0)
    )

    fun selectDailyEvaluation(day: DailyEvaluation?) {
        _uiState.value = _uiState.value.copy(selectedDailyEvaluation = day)
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectFilter(filter: MemoryFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun openMemoryDetail(memory: EventMemory?) {
        _uiState.value = _uiState.value.copy(selectedMemory = memory)
    }

    fun openEveningRecallFor(memory: EventMemory) {
        _uiState.value = _uiState.value.copy(
            eveningTargetMemory = memory,
            currentTab = NavigationTab.NOCTURNE
        )
    }

    fun openSettings(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSettingsDialog = show)
    }

    fun openUserGuide(show: Boolean) {
        _uiState.value = _uiState.value.copy(showUserGuideDialog = show)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(analysisError = null)
    }

    fun createInstantMemory(
        title: String,
        description: String,
        imageContext: String,
        photoUri: String?,
        emotion: String,
        location: String
    ) {
        val newMem = repository.addInstantMemory(
            title = title.ifBlank { "Souvenir du moment" },
            instantDescription = description,
            imageContextDescription = imageContext,
            photoUri = photoUri,
            emotionalState = emotion,
            location = location
        )
        // Switch back to Journal to show the newly created card
        _uiState.value = _uiState.value.copy(currentTab = NavigationTab.JOURNAL)
    }

    fun submitEveningRecall(
        memoryId: String,
        eveningDescription: String,
        confidenceRating: Int,
        runAnalysisNow: Boolean = true
    ) {
        repository.updateEveningRecall(memoryId, eveningDescription, confidenceRating)
        _uiState.value = _uiState.value.copy(eveningTargetMemory = null)

        if (runAnalysisNow) {
            runAnalysis(memoryId)
        } else {
            _uiState.value = _uiState.value.copy(currentTab = NavigationTab.JOURNAL)
        }
    }

    fun runAnalysis(memoryId: String) {
        val memory = allMemories.value.find { it.id == memoryId } ?: return
        val eveningDesc = memory.eveningDescription ?: return

        _uiState.value = _uiState.value.copy(
            isAnalyzing = true,
            analyzingMemoryId = memoryId,
            analysisError = null
        )

        viewModelScope.launch {
            val result = aiService.analyzeEvent(
                instantDescription = memory.instantDescription,
                imageContextDescription = memory.imageContextDescription,
                eveningDescription = eveningDesc,
                timestamp = memory.timestampInstant
            )

            result.onSuccess { analysis ->
                repository.saveAnalysis(memoryId, analysis)
                val updatedMemory = repository.memories.value.find { it.id == memoryId }
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    analyzingMemoryId = null,
                    selectedMemory = updatedMemory,
                    currentTab = NavigationTab.JOURNAL
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    analyzingMemoryId = null,
                    analysisError = error.message ?: "Erreur d'analyse cognitive"
                )
            }
        }
    }

    fun toggleFavorite(memoryId: String) {
        repository.toggleFavorite(memoryId)
        val currentSelected = _uiState.value.selectedMemory
        if (currentSelected?.id == memoryId) {
            _uiState.value = _uiState.value.copy(
                selectedMemory = currentSelected.copy(isFavorite = !currentSelected.isFavorite)
            )
        }
    }

    fun deleteMemory(memoryId: String) {
        repository.deleteMemory(memoryId)
        if (_uiState.value.selectedMemory?.id == memoryId) {
            _uiState.value = _uiState.value.copy(selectedMemory = null)
        }
    }

    fun resetToSamples() {
        repository.resetToSamples()
    }

    fun saveApiSettings(apiKey: String, model: String, customUrl: String) {
        preferences.apiKey = apiKey
        preferences.model = model
        preferences.customUrl = customUrl
        _uiState.value = _uiState.value.copy(showSettingsDialog = false)
    }
}
