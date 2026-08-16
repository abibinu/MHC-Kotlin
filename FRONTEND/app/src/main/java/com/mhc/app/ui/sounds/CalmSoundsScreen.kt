package com.mhc.app.ui.sounds

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mhc.app.data.model.SoundTrack
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalmSoundsScreen(
    onBack: () -> Unit,
    viewModel: CalmSoundsViewModel = viewModel()
) {
    val categories = listOf("All", "Nature", "Acoustic", "Focus")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calm Sounds Hub",
                        fontWeight = FontWeight.Bold,
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
                .background(Color(0xFFF7F9F8))
        ) {
            // Audio Player Deck Card
            val track = viewModel.currentTrack
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Album Art Box
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(track.colorHex),
                                        Color(0xFF004D40)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = track.iconEmoji, fontSize = 48.sp)
                            if (viewModel.isPlaying) {
                                Spacer(modifier = Modifier.height(4.dp))
                                AudioWaveformBars()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = track.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.artist,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFE0F2F1),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = track.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Slider
                    Slider(
                        value = viewModel.currentPositionMs.toFloat(),
                        onValueChange = { viewModel.seekTo(it) },
                        valueRange = 0f..viewModel.durationMs.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00796B),
                            activeTrackColor = Color(0xFF00796B),
                            inactiveTrackColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(viewModel.currentPositionMs),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTime(viewModel.durationMs),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Control Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.playPrevious() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = Color(0xFF00796B),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.togglePlayPause() },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00796B))
                        ) {
                            if (viewModel.isBuffering) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.playNext() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = Color(0xFF00796B),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = viewModel.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(text = category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00796B),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playlist Track Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredTracks, key = { it.id }) { soundTrack ->
                    SoundTrackCard(
                        track = soundTrack,
                        isCurrent = viewModel.currentTrack.id == soundTrack.id,
                        isPlaying = viewModel.isPlaying && viewModel.currentTrack.id == soundTrack.id,
                        onClick = { viewModel.playTrack(soundTrack) }
                    )
                }
            }
        }
    }
}

@Composable
fun AudioWaveformBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val heightScale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveHeight"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp * heightScale)
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp * (1.3f - heightScale).coerceIn(0.2f, 1f))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp * heightScale * 0.8f)
                .background(Color.White)
        )
    }
}

@Composable
fun SoundTrackCard(
    track: SoundTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFFE0F2F1) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrent) 1.5.dp else 0.dp,
                color = if (isCurrent) Color(0xFF00796B) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(track.colorHex).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = track.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${track.category} • ${formatTime(track.durationSeconds * 1000)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) Color(0xFF00796B) else Color(0xFFF0F4F3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play Track",
                    tint = if (isCurrent) Color.White else Color(0xFF00796B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatTime(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
