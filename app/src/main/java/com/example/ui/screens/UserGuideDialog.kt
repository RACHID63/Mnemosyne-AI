package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FidelityHighGreen
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SynapticCyan

@Composable
fun UserGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_guide_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SynapticCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SynapticCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mode d'emploi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Protocole Mnemosyne-AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = SynapticCyan
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Mnemosyne-AI mesure l'écart entre la réalité objective d'un événement et la façon dont votre cerveau le reconstruit en fin de journée.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Étape 1
                GuideStepCard(
                    stepNumber = "1",
                    title = "Capture à Chaud (Pendant l'événement)",
                    icon = Icons.Default.AddCircle,
                    iconTint = SynapticCyan,
                    description = "Dès qu'un moment survient (réunion, repas, échange, surprise), ouvrez l'onglet « À chaud ». Notez les faits bruts, l'émotion ressentie et les détails visuels (ou photo). L'événement est alors scellé pour ce soir."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Étape 2
                GuideStepCard(
                    stepNumber = "2",
                    title = "Récit Nocturne (En fin de journée)",
                    icon = Icons.Default.NightsStay,
                    iconTint = MemoryGold,
                    description = "Le soir, allez dans l'onglet « De mémoire ». Sans relire votre note du matin (Mode Aveugle Cognitif), écrivez l'événement tel que vous vous en souvenez maintenant, et indiquez votre niveau de confiance."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Étape 3
                GuideStepCard(
                    stepNumber = "3",
                    title = "Analyse & Révélation des Biais",
                    icon = Icons.Default.Psychology,
                    iconTint = NeuroViolet,
                    description = "Mnemosyne compare sémantiquement les deux versions et calcule votre score de fidélité (0-100%). Elle identifie les omissions, les convergences et les biais cognitifs (dramatisation, embellissement, etc.)."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Étape 4
                GuideStepCard(
                    stepNumber = "4",
                    title = "Sciences & Métacognition",
                    icon = Icons.Default.AutoAwesome,
                    iconTint = FidelityHighGreen,
                    description = "Explorez l'onglet « Sciences » pour suivre la distribution de vos biais cognitifs et comprendre les mécanismes de reconstruction mémorielle d'après les neurosciences."
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Les 3 Règles d'Or
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeuroViolet.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 LES 3 RÈGLES D'OR DU PROTOCOLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeuroViolet
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RuleItem(
                            number = "1",
                            text = "Ne trichez pas : ne relisez pas la note à chaud avant de rédiger le soir."
                        )
                        RuleItem(
                            number = "2",
                            text = "Soyez précis à chaud : notez des détails concrets (chiffres, vêtements, mots dits)."
                        )
                        RuleItem(
                            number = "3",
                            text = "Soyez bienveillant : déformer un souvenir est un mécanisme cérébral tout à fait normal !"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeuroViolet),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("close_guide_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("J'ai compris le protocole", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}

@Composable
private fun GuideStepCard(
    stepNumber: String,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RuleItem(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            fontWeight = FontWeight.Bold,
            color = SynapticCyan
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
