package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SynapticCyan
import com.example.ui.theme.getEmotionColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InstantCaptureScreen(
    onSaveInstant: (
        title: String,
        description: String,
        imageContext: String,
        photoUri: String?,
        emotion: String,
        location: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageContext by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf("Calme") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val emotions = listOf(
        "Calme", "Joyeux", "Stressé", "Concentré", "Surpris",
        "Fatigué", "Frustré", "Émerveillé", "Inquiet", "Détendu"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Capture à Chaud",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Enregistrez la réalité objective au moment exact où elle se produit",
            style = MaterialTheme.typography.bodySmall,
            color = SynapticCyan
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cognitive Sealing Notice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SynapticCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ce récit restera masqué jusqu'à ce soir pour préserver l'authenticité de votre rappel mémoriel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errorMsg = null },
            label = { Text("Titre de l'événement (ex: Réunion client, Déjeuner)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("instant_title_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SynapticCyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Instant Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; errorMsg = null },
            label = { Text("Que se passe-t-il exactement maintenant ? (Faits bruts)") },
            placeholder = { Text("Ex: Marc annonce 12 min de retard. Sophie fronce les sourcils mais accepte de revoir le planning...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("instant_description_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 6,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SynapticCyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Context / Photo Description
        OutlinedTextField(
            value = imageContext,
            onValueChange = { imageContext = it },
            label = { Text("Contexte visuel / Détails de la scène") },
            placeholder = { Text("Ex: Tableau blanc avec diagramme bleu, tasse en carton renversée sur la table, lumière tamisée...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("instant_visual_context_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SynapticCyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Photo Picker Section
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
                        text = "Photo témoin de l'instant",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SynapticCyan),
                        modifier = Modifier.testTag("pick_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ajouter", color = Color.Black, fontSize = 12.sp)
                    }
                }

                if (selectedImageUri != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Photo de l'instant",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Supprimer photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Emotion selector
        Text(
            text = "État émotionnel au moment vécu :",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            emotions.forEach { emotion ->
                val col = getEmotionColor(emotion)
                FilterChip(
                    selected = emotion == selectedEmotion,
                    onClick = { selectedEmotion = emotion },
                    label = { 
                        Text(
                            text = emotion,
                            fontWeight = if (emotion == selectedEmotion) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = col,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("emotion_chip_$emotion")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Location Field
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { Text("Lieu / Environnement") },
            placeholder = { Text("Ex: Bureau salle B12, Terrasse de café...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("instant_location_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMsg ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                if (description.isBlank()) {
                    errorMsg = "Veuillez décrire brièvement ce qui se passe."
                    return@Button
                }
                onSaveInstant(
                    title.ifBlank { "Événement du jour" },
                    description.trim(),
                    imageContext.trim(),
                    selectedImageUri?.toString(),
                    selectedEmotion,
                    location.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_instant_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sceller l'instant brut pour ce soir",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
