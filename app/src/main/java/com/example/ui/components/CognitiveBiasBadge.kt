package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SynapticCyan

@Composable
fun CognitiveBiasCard(
    biasText: String,
    modifier: Modifier = Modifier
) {
    val isDramatization = biasText.contains("dramatisation", ignoreCase = true)
    val isEmbellishment = biasText.contains("embellissement", ignoreCase = true) || biasText.contains("positivité", ignoreCase = true)
    val isFaithful = biasText.contains("fidèle", ignoreCase = true) || biasText.contains("conservatrice", ignoreCase = true)

    val (accentColor, backgroundColor) = when {
        isDramatization -> Pair(Color(0xFFFF758F), Color(0x22FF758F))
        isEmbellishment -> Pair(MemoryGold, Color(0x22FFD166))
        isFaithful -> Pair(SynapticCyan, Color(0x224EE0ED))
        else -> Pair(NeuroViolet, Color(0x22B58EFF))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("cognitive_bias_card")
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFaithful) Icons.Default.AutoAwesome else Icons.Default.Psychology,
                    contentDescription = "Biais cognitif",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BIAIS COGNITIF DÉTECTÉ",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = biasText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
