package com.mhc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mhc.app.data.api.RetrofitClient
import com.mhc.app.data.session.UserSessionManager
import com.mhc.app.ui.auth.AuthScreen
import com.mhc.app.ui.chat.ChatScreen
import com.mhc.app.ui.emergency.EmergencyHelpScreen
import com.mhc.app.ui.home.HomeScreen
import com.mhc.app.ui.mood.MoodTrackerScreen
import com.mhc.app.ui.relaxation.RelaxationGameScreen
import com.mhc.app.ui.sounds.CalmSoundsScreen
import com.mhc.app.ui.tasks.TasksScreen
import com.mhc.app.ui.theme.MentalHealthCompanionTheme

enum class ScreenState {
    AUTH,
    HOME,
    CHAT,
    MOOD,
    RELAXATION,
    TASKS,
    EMERGENCY,
    CALM_SOUNDS
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MentalHealthCompanionTheme {
                val context = LocalContext.current
                val sessionManager = remember { UserSessionManager(context) }

                LaunchedEffect(Unit) {
                    RetrofitClient.setBaseUrl(sessionManager.getServerBaseUrl())
                }

                var currentScreen by remember {
                    mutableStateOf(if (sessionManager.isLoggedIn()) ScreenState.HOME else ScreenState.AUTH)
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen, label = "ScreenCrossfade") { screen ->
                        when (screen) {
                            ScreenState.AUTH -> {
                                AuthScreen(
                                    onAuthSuccess = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.HOME -> {
                                HomeScreen(
                                    onLogout = {
                                        currentScreen = ScreenState.AUTH
                                    },
                                    onFeatureClick = { featureTitle ->
                                        when (featureTitle) {
                                            "AI Virtual Therapist" -> currentScreen = ScreenState.CHAT
                                            "Mood Analytics" -> currentScreen = ScreenState.MOOD
                                            "Calm Sounds Hub" -> currentScreen = ScreenState.CALM_SOUNDS
                                            "Emergency Help" -> currentScreen = ScreenState.EMERGENCY
                                            "Daily Task Planner" -> currentScreen = ScreenState.TASKS
                                            "Relaxation Breathing" -> currentScreen = ScreenState.RELAXATION
                                        }
                                    }
                                )
                            }
                            ScreenState.CHAT -> {
                                ChatScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.MOOD -> {
                                MoodTrackerScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.RELAXATION -> {
                                RelaxationGameScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.TASKS -> {
                                TasksScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.EMERGENCY -> {
                                EmergencyHelpScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                            ScreenState.CALM_SOUNDS -> {
                                CalmSoundsScreen(
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
