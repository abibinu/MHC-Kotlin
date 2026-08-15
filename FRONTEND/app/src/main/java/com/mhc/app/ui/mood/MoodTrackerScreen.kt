package com.mhc.app.ui.mood

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mhc.app.data.model.MoodLogItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(
    onBack: () -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mood Tracker & Analytics",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F9F8))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // How are you feeling today card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How are you feeling today?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Select your current emotional state below",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mood Options Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            viewModel.availableMoods.take(3).forEach { option ->
                                MoodOptionTile(
                                    option = option,
                                    isSelected = viewModel.selectedMood?.name == option.name,
                                    onClick = { viewModel.selectMood(option) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            viewModel.availableMoods.drop(3).forEach { option ->
                                MoodOptionTile(
                                    option = option,
                                    isSelected = viewModel.selectedMood?.name == option.name,
                                    onClick = { viewModel.selectMood(option) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Personal Reflection Note Input
                        OutlinedTextField(
                            value = viewModel.noteInput,
                            onValueChange = { viewModel.noteInput = it },
                            placeholder = { Text("Add a personal note or reflection (optional)...") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Success / Error Banner
                        viewModel.successMessage?.let { msg ->
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Log Mood Action Button
                        Button(
                            onClick = { viewModel.logMood() },
                            enabled = !viewModel.isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Log Mood")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOG MOOD ENTRY",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Mood Analytics Donut Chart & Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mood Analytics Breakdown",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "${viewModel.analyticsMap.values.sum()} Total Logs",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00796B)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.analyticsMap.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Custom Compose Donut Chart
                                DonutChart(
                                    data = viewModel.analyticsMap,
                                    moodOptions = viewModel.availableMoods,
                                    modifier = Modifier.size(120.dp)
                                )

                                Spacer(modifier = Modifier.width(20.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    val totalCount = viewModel.analyticsMap.values.sum().toFloat().coerceAtLeast(1f)
                                    viewModel.analyticsMap.forEach { (moodName, count) ->
                                        val option = viewModel.availableMoods.find { it.name == moodName }
                                        val percentage = ((count / totalCount) * 100).toInt()
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            option?.colorHex?.let { Color(it) } ?: Color.Gray
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${option?.emoji ?: ""} $moodName",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            Text(
                                                text = "$count ($percentage%)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF555555)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No mood history recorded yet.",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Recent Logs Timeline Header
            item {
                Text(
                    text = "Recent Mood History",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }

            // Mood Logs History List
            items(viewModel.moodHistory) { item ->
                MoodHistoryCard(item = item, availableMoods = viewModel.availableMoods)
            }
        }
    }
}

@Composable
fun MoodOptionTile(
    option: MoodOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(option.colorHex).copy(alpha = 0.35f) else Color(0xFFF2F4F3)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .padding(horizontal = 4.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF00796B) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = option.emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun DonutChart(
    data: Map<String, Int>,
    moodOptions: List<MoodOption>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum().toFloat().coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        var startAngle = -90f
        data.forEach { (moodName, count) ->
            val sweepAngle = (count / total) * 360f
            val option = moodOptions.find { it.name == moodName }
            val color = option?.colorHex?.let { Color(it) } ?: Color.Gray

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun MoodHistoryCard(
    item: MoodLogItem,
    availableMoods: List<MoodOption>
) {
    val option = availableMoods.find { it.name == item.mood }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(option?.colorHex?.let { Color(it).copy(alpha = 0.25f) } ?: Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = option?.emoji ?: "😐", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.mood,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    item.loggedAt?.let { dateStr ->
                        Text(
                            text = dateStr.take(16),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                item.note?.let { noteText ->
                    if (noteText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"$noteText\"",
                            fontSize = 12.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        }
    }
}
