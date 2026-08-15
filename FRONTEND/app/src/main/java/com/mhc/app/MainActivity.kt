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
import com.mhc.app.data.session.UserSessionManager
import com.mhc.app.ui.auth.AuthScreen
import com.mhc.app.ui.chat.ChatScreen
import com.mhc.app.ui.home.HomeScreen
import com.mhc.app.ui.mood.MoodTrackerScreen
import com.mhc.app.ui.relaxation.RelaxationGameScreen
import com.mhc.app.ui.theme.MentalHealthCompanionTheme

enum class ScreenState {
    AUTH,
    HOME,
    CHAT,
    MOOD,
    RELAXATION
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
                        }
                    }
                }
            }
        }
    }
}
