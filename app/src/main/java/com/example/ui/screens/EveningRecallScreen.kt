package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EventMemory
import com.example.model.MemoryStatus
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SynapticCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EveningRecallScreen(
    memories: List<EventMemory>,
    targetMemory: EventMemory?,
    onSubmitRecall: (memoryId: String, eveningDescription: String, confidenceRating: Int, analyzeNow: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingList = memories.filter { it.status == MemoryStatus.EN_ATTENTE_SOIR || it.status == MemoryStatus.PRET_ANALYSE }
    var selectedMemoryId by remember {
        mutableStateOf(targetMemory?.id ?: pendingList.firstOrNull()?.id ?: "")
    }

    LaunchedEffect(targetMemory) {
        if (targetMemory != null) {
            selectedMemoryId = targetMemory.id
        }
    }

    val currentMemory = memories.find { it.id == selectedMemoryId } ?: pendingList.firstOrNull()

    var eveningText by remember(selectedMemoryId) {
        mutableStateOf(currentMemory?.eveningDescription ?: "")
    }
    var confidence by remember(selectedMemoryId) {
        mutableIntStateOf(currentMemory?.confidenceRating ?: 3)
    }
    var showHint by remember(selectedMemoryId) { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("HH:mm", Locale.FRENCH)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.NightsStay,
                contentDescription = null,
                tint = MemoryGold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Récit Nocturne",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Reconstruisez l'événement uniquement à partir de vos souvenirs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MemoryGold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentMemory == null) {
            // Empty state: no pending memories
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = MemoryGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucun événement en attente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Capturez d'abord un événement dans l'onglet 'À chaud' au moment où il a lieu pour tester votre mémoire ce soir.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Event Selector if multiple are available
            if (pendingList.size > 1) {
                Text(
                    text = "Sélectionnez l'événement à raconter :",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pendingList.forEach { m ->
                        val isSelected = m.id == currentMemory.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MemoryGold else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedMemoryId = m.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = m.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Memory blind target card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ÉVÉNEMENT CIBLÉ (MODE AVEUGLE)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MemoryGold
                        )
                        Text(
                            text = "Capturé à ${dateFormat.format(Date(currentMemory.timestampInstant))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentMemory.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cognitive blind mode hint toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { showHint = !showHint },
                            modifier = Modifier.testTag("toggle_hint_button")
                        ) {
                            Icon(
                                imageVector = if (showHint) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MemoryGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showHint) "Masquer l'indice" else "Besoin d'un indice contextuel ?",
                                color = MemoryGold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (showHint) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            Column {
                                if (currentMemory.locationInstant.isNotBlank()) {
                                    Text(
                                        text = "📍 Lieu : ${currentMemory.locationInstant}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "🎭 Émotion ressentie : ${currentMemory.emotionalStateInstant}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Evening recall text area
            OutlinedTextField(
                value = eveningText,
                onValueChange = { eveningText = it; errorMessage = null },
                label = { Text("Votre récit de mémoire (tel que vous vous en souvenez)") },
                placeholder = {
                    Text(
                        "Racontez l'événement avec vos propres mots : qui était là ? que s'est-il passé ? quelles étaient les paroles ou attitudes ?"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("evening_recall_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MemoryGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confidence rating
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Niveau de confiance mémorielle :",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val confidenceLabel = when (confidence) {
                            1 -> "Très flou"
                            2 -> "Incertain"
                            3 -> "Moyennement sûr"
                            4 -> "Confiant"
                            else -> "Certitude absolue"
                        }
                        Text(
                            text = confidenceLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MemoryGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= confidence) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Confiance $i/5",
                                tint = if (i <= confidence) MemoryGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { confidence = i }
                                    .padding(4.dp)
                                    .testTag("confidence_star_$i")
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action: Submit & Analyze
            Button(
                onClick = {
                    if (eveningText.isBlank()) {
                        errorMessage = "Veuillez rédiger quelques lignes de mémoire pour permettre la comparaison."
                        return@Button
                    }
                    onSubmitRecall(currentMemory.id, eveningText.trim(), confidence, true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_and_analyze_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enregistrer & Analyser avec Mnemosyne-AI",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
