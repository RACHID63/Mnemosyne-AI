package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyEvaluation
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FidelityHighGreen
import com.example.ui.theme.FidelityLowRed
import com.example.ui.theme.FidelityMediumAmber
import com.example.ui.theme.MemoryGold
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeuroViolet
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SynapticCyan
import com.example.ui.theme.VividViolet

@Composable
fun PerformanceTimelineChart(
    evaluations: List<DailyEvaluation>,
    selectedDay: DailyEvaluation?,
    onSelectDay: (DailyEvaluation) -> Unit,
    modifier: Modifier = Modifier
) {
    if (evaluations.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = SynapticCyan,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Timeline des performances en attente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Évaluez votre premier événement pour tracer votre courbe de fidélité mémorielle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(evaluations.size) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    var tappedIndex by remember {
        mutableStateOf(evaluations.indexOf(selectedDay).takeIf { it >= 0 } ?: (evaluations.size - 1))
    }

    val activeDay = evaluations.getOrNull(tappedIndex) ?: evaluations.last()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("performance_timeline_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Title & Selected Day Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SynapticCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = SynapticCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TIMELINE DES PERFORMANCES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SynapticCyan
                        )
                        Text(
                            text = "Évolution de la fidélité dans le temps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Score pill for the active day
                val pillColor = when {
                    activeDay.averageScore >= 80 -> FidelityHighGreen
                    activeDay.averageScore >= 50 -> FidelityMediumAmber
                    else -> FidelityLowRed
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(pillColor.copy(alpha = 0.2f))
                        .border(1.dp, pillColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${activeDay.averageScore}% (${activeDay.dateLabel})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = pillColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Canvas Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(evaluations) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val paddingX = 40f
                                val usableWidth = width - 2 * paddingX
                                val stepX = if (evaluations.size > 1) {
                                    usableWidth / (evaluations.size - 1)
                                } else 0f

                                val closestIndex = evaluations.indices.minByOrNull { i ->
                                    val x = paddingX + i * stepX
                                    kotlin.math.abs(x - offset.x)
                                } ?: 0

                                tappedIndex = closestIndex
                                onSelectDay(evaluations[closestIndex])
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val paddingLeft = 40f
                    val paddingRight = 40f
                    val paddingTop = 24f
                    val paddingBottom = 32f

                    val chartWidth = w - paddingLeft - paddingRight
                    val chartHeight = h - paddingTop - paddingBottom

                    // Draw reference grid lines (80% and 50%)
                    val y80 = paddingTop + chartHeight * (1f - 0.8f)
                    val y50 = paddingTop + chartHeight * (1f - 0.5f)
                    val y0 = paddingTop + chartHeight

                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    val gridColor = Color(0x33B58EFF)

                    // 80% line
                    drawLine(
                        color = gridColor,
                        start = Offset(paddingLeft, y80),
                        end = Offset(w - paddingRight, y80),
                        pathEffect = dashedEffect,
                        strokeWidth = 1.5f
                    )

                    // 50% line
                    drawLine(
                        color = gridColor.copy(alpha = 0.15f),
                        start = Offset(paddingLeft, y50),
                        end = Offset(w - paddingRight, y50),
                        pathEffect = dashedEffect,
                        strokeWidth = 1f
                    )

                    // Base axis line
                    drawLine(
                        color = Color(0x44FFFFFF),
                        start = Offset(paddingLeft, y0),
                        end = Offset(w - paddingRight, y0),
                        strokeWidth = 1.5f
                    )

                    // Calculate point coordinates
                    val stepX = if (evaluations.size > 1) chartWidth / (evaluations.size - 1) else 0f
                    val points = evaluations.mapIndexed { index, item ->
                        val x = if (evaluations.size > 1) paddingLeft + index * stepX else w / 2f
                        val normalizedScore = (item.averageScore / 100f).coerceIn(0f, 1f)
                        val y = paddingTop + chartHeight * (1f - normalizedScore * animatedProgress.value)
                        Offset(x, y)
                    }

                    if (points.isNotEmpty()) {
                        // Smooth Cubic Bezier Line Path
                        val linePath = Path()
                        val fillPath = Path()

                        linePath.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, y0)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx1 = p0.x + (p1.x - p0.x) / 2f
                            val cy1 = p0.y
                            val cx2 = p0.x + (p1.x - p0.x) / 2f
                            val cy2 = p1.y
                            linePath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                            fillPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                        }

                        fillPath.lineTo(points.last().x, y0)
                        fillPath.close()

                        // Draw Gradient Area under Curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    NeonMagenta.copy(alpha = 0.35f * animatedProgress.value),
                                    ElectricCyan.copy(alpha = 0.18f * animatedProgress.value),
                                    Color.Transparent
                                ),
                                startY = paddingTop,
                                endY = y0
                            )
                        )

                        // Draw Curved Stroke with Multi-Color Aurora
                        drawPath(
                            path = linePath,
                            brush = Brush.horizontalGradient(
                                colors = listOf(NeonMagenta, VividViolet, ElectricCyan, SolarGold),
                                startX = paddingLeft,
                                endX = w - paddingRight
                            ),
                            style = Stroke(
                                width = 4.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw Glowing Nodes for each evaluated day
                        points.forEachIndexed { i, pt ->
                            val score = evaluations[i].averageScore
                            val isSelected = i == tappedIndex
                            val dotColor = when {
                                score >= 80 -> FidelityHighGreen
                                score >= 50 -> FidelityMediumAmber
                                else -> FidelityLowRed
                            }

                            // Outer halo for selected node
                            if (isSelected) {
                                drawCircle(
                                    color = dotColor.copy(alpha = 0.35f),
                                    radius = 14.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 8.dp.toPx(),
                                    center = pt
                                )
                            }

                            // Inner dot
                            drawCircle(
                                color = dotColor,
                                radius = (if (isSelected) 6.dp else 4.5.dp).toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // X-Axis Day Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                evaluations.forEachIndexed { index, day ->
                    val isSelected = index == tappedIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SynapticCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = day.dayOfMonth,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day Inspection Sub-Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = SynapticCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bilan du ${activeDay.dateLabel} (${activeDay.dayOfWeek} ${activeDay.dayOfMonth})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${activeDay.evaluatedCount} souvenir${if (activeDay.evaluatedCount > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Biais dominant :",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = activeDay.dominantBias,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NeuroViolet
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Score du jour :",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val scoreColor = when {
                                activeDay.averageScore >= 80 -> FidelityHighGreen
                                activeDay.averageScore >= 50 -> FidelityMediumAmber
                                else -> FidelityLowRed
                            }
                            Text(
                                text = "${activeDay.averageScore}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                        }
                    }
                }
            }
        }
    }
}
