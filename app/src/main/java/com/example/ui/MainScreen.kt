package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AnalysisDetailScreen
import com.example.ui.screens.EveningRecallScreen
import com.example.ui.screens.InstantCaptureScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.UserGuideDialog
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SynapticCyan
import com.example.ui.theme.VividViolet

@Composable
fun MainScreen(
    viewModel: MnemosyneViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredMemories by viewModel.filteredMemories.collectAsState()
    val allMemories by viewModel.allMemories.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()
    val averageAccuracy by viewModel.averageAccuracy.collectAsState()
    val dailyEvaluations by viewModel.dailyEvaluations.collectAsState()
    val performanceMetrics by viewModel.performanceMetrics.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.analysisError) {
        uiState.analysisError?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Show bottom navigation only when not viewing analysis details
            if (uiState.selectedMemory == null) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    // Tab 1 : Journal
                    NavigationBarItem(
                        selected = uiState.currentTab == NavigationTab.JOURNAL,
                        onClick = { viewModel.selectTab(NavigationTab.JOURNAL) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Journal"
                            )
                        },
                        label = { Text("Journal") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = NeonMagenta,
                            indicatorColor = NeonMagenta
                        ),
                        modifier = Modifier.testTag("nav_tab_journal")
                    )

                    // Tab 2 : À chaud
                    NavigationBarItem(
                        selected = uiState.currentTab == NavigationTab.CAPTURE,
                        onClick = { viewModel.selectTab(NavigationTab.CAPTURE) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "À chaud"
                            )
                        },
                        label = { Text("À chaud") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan
                        ),
                        modifier = Modifier.testTag("nav_tab_capture")
                    )

                    // Tab 3 : De mémoire (Nocturne)
                    NavigationBarItem(
                        selected = uiState.currentTab == NavigationTab.NOCTURNE,
                        onClick = { viewModel.selectTab(NavigationTab.NOCTURNE) },
                        icon = {
                            if (pendingCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = SolarGold,
                                            contentColor = Color.Black
                                        ) {
                                            Text("$pendingCount")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NightsStay,
                                        contentDescription = "De mémoire"
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = "De mémoire"
                                )
                            }
                        },
                        label = { Text("De mémoire") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SolarGold,
                            indicatorColor = SolarGold
                        ),
                        modifier = Modifier.testTag("nav_tab_nocturne")
                    )

                    // Tab 4 : Sciences
                    NavigationBarItem(
                        selected = uiState.currentTab == NavigationTab.INSIGHTS,
                        onClick = { viewModel.selectTab(NavigationTab.INSIGHTS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Sciences"
                            )
                        },
                        label = { Text("Sciences") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = VividViolet,
                            indicatorColor = VividViolet
                        ),
                        modifier = Modifier.testTag("nav_tab_insights")
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val selectedMem = uiState.selectedMemory
            if (selectedMem != null) {
                AnalysisDetailScreen(
                    memory = selectedMem,
                    onBack = { viewModel.openMemoryDetail(null) },
                    onReanalyze = { id -> viewModel.runAnalysis(id) },
                    onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                    isAnalyzing = uiState.isAnalyzing
                )
            } else {
                when (uiState.currentTab) {
                    NavigationTab.JOURNAL -> {
                        JournalScreen(
                            memories = filteredMemories,
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelect = { viewModel.selectFilter(it) },
                            onMemoryClick = { viewModel.openMemoryDetail(it) },
                            onEveningRecallClick = { viewModel.openEveningRecallFor(it) },
                            onAnalyzeClick = { viewModel.runAnalysis(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onOpenSettings = { viewModel.openSettings(true) },
                            onOpenGuide = { viewModel.openUserGuide(true) },
                            onResetSamples = { viewModel.resetToSamples() },
                            onNavigateToTracking = { viewModel.selectTab(NavigationTab.INSIGHTS) },
                            averageAccuracy = averageAccuracy,
                            pendingCount = pendingCount
                        )
                    }
                    NavigationTab.CAPTURE -> {
                        InstantCaptureScreen(
                            onSaveInstant = { title, desc, visualCtx, uri, emotion, loc ->
                                viewModel.createInstantMemory(title, desc, visualCtx, uri, emotion, loc)
                            }
                        )
                    }
                    NavigationTab.NOCTURNE -> {
                        EveningRecallScreen(
                            memories = allMemories,
                            targetMemory = uiState.eveningTargetMemory,
                            onSubmitRecall = { id, desc, rating, analyzeNow ->
                                viewModel.submitEveningRecall(id, desc, rating, analyzeNow)
                            }
                        )
                    }
                    NavigationTab.INSIGHTS -> {
                        InsightsScreen(
                            memories = allMemories,
                            dailyEvaluations = dailyEvaluations,
                            performanceMetrics = performanceMetrics,
                            selectedDay = uiState.selectedDailyEvaluation,
                            onSelectDay = { viewModel.selectDailyEvaluation(it) },
                            onMemoryClick = { viewModel.openMemoryDetail(it) },
                            onOpenGuide = { viewModel.openUserGuide(true) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            preferences = viewModel.preferences,
            onDismiss = { viewModel.openSettings(false) },
            onSave = { key, model, url ->
                viewModel.saveApiSettings(key, model, url)
            },
            onResetSamples = { viewModel.resetToSamples() }
        )
    }

    if (uiState.showUserGuideDialog) {
        UserGuideDialog(
            onDismiss = { viewModel.openUserGuide(false) }
        )
    }
}
