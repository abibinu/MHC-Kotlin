package com.mhc.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhc.app.data.session.UserSessionManager

data class HomeFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String,
    val containerColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onFeatureClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = UserSessionManager(context)
    val userName = sessionManager.getUserName()
    val userEmail = sessionManager.getUserEmail()

    val features = listOf(
        HomeFeature(
            title = "AI Virtual Therapist",
            description = "Bilingual (English / Malayalam) Gemini AI guidance",
            icon = Icons.Default.AutoAwesome,
            badge = "Gemini AI",
            containerColor = Color(0xFFE8F5E9)
        ),
        HomeFeature(
            title = "Mood Analytics",
            description = "Log daily feelings & track emotional trends",
            icon = Icons.Default.Favorite,
            badge = "Tracker",
            containerColor = Color(0xFFFFF3E0)
        ),
        HomeFeature(
            title = "Calm Sounds Hub",
            description = "Streaming rain, nature & acoustic soundscapes",
            icon = Icons.Default.Headphones,
            badge = "Audio",
            containerColor = Color(0xFFE1F5FE)
        ),
        HomeFeature(
            title = "Emergency Help",
            description = "One-tap crisis hotlines & emergency dialer",
            icon = Icons.Default.PhoneInTalk,
            badge = "24/7 Hotlines",
            containerColor = Color(0xFFFFEBEE)
        ),
        HomeFeature(
            title = "Daily Task Planner",
            description = "Mindfulness checklists & habit achievements",
            icon = Icons.Default.CheckCircle,
            badge = "Habits",
            containerColor = Color(0xFFF3E5F5)
        ),
        HomeFeature(
            title = "Relaxation Breathing",
            description = "Interactive Calm Tap breathing exercise",
            icon = Icons.Default.SelfImprovement,
            badge = "Mindfulness",
            containerColor = Color(0xFFEFEBE9)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mental Health Companion",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        sessionManager.clearSession()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F9F8))
                .padding(16.dp)
        ) {
            // Welcome User Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00796B), Color(0xFF004D40))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Welcome back, $userName!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (userEmail.isNotBlank()) userEmail else "Logged in securely",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Explore Companion Tools",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of Feature Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(features) { feature ->
                    FeatureGridCard(feature = feature, onClick = { onFeatureClick(feature.title) })
                }
            }
        }
    }
}

@Composable
fun FeatureGridCard(feature: HomeFeature, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(feature.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = feature.badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = feature.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = feature.description,
                    fontSize = 11.sp,
                    color = Color(0xFF666666),
                    maxLines = 2
                )
            }
        }
    }
}
