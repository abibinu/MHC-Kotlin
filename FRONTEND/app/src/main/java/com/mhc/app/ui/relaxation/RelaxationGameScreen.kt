package com.mhc.app.ui.relaxation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelaxationGameScreen(
    onBack: () -> Unit,
    viewModel: RelaxationViewModel = viewModel()
) {
    val animatedScale by animateFloatAsState(
        targetValue = viewModel.circleScale,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "BreathingCircleScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calm Tap Breathing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF4F7F6))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Instruction Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Mindfulness Icon",
                            tint = Color(0xFF00796B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rhythmic Breathing Exercise",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "3s Inhale • 2s Hold • 3s Exhale",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Animated Pulsating Circle Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow Ring
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(animatedScale * 1.15f)
                        .clip(CircleShape)
                        .background(Color(0xFF80CBC4).copy(alpha = 0.25f))
                )

                // Middle Glow Ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(animatedScale * 1.05f)
                        .clip(CircleShape)
                        .background(Color(0xFF4DB6AC).copy(alpha = 0.4f))
                )

                // Core Interactive Breathing Circle
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(animatedScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00897B),
                                    Color(0xFF004D40)
                                )
                            )
                        )
                        .clickable {
                            viewModel.onTapBeat()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (viewModel.currentPhase) {
                                BreathingPhase.IDLE -> "START"
                                BreathingPhase.INHALE -> "INHALE"
                                BreathingPhase.HOLD -> "HOLD"
                                BreathingPhase.EXHALE -> "EXHALE"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (viewModel.isSessionActive) "Tap to focus" else "Tap Start below",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Mindfulness Tip & Cycle Stats Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Cycles Completed",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${viewModel.completedCycles}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00796B)
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Mindfulness Score",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${viewModel.tapScore} pts",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00796B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "\"${viewModel.currentTip}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.startOrPauseSession() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isSessionActive) Color(0xFFFF7043) else Color(0xFF00796B)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.isSessionActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start Pause"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (viewModel.isSessionActive) "PAUSE" else "START",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.resetSession() },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color(0xFF00796B)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RESET",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
