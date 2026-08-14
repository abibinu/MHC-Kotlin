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
import com.mhc.app.ui.home.HomeScreen
import com.mhc.app.ui.theme.MentalHealthCompanionTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MentalHealthCompanionTheme {
                val context = LocalContext.current
                val sessionManager = remember { UserSessionManager(context) }
                var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = isLoggedIn, label = "AuthCrossfade") { loggedInState ->
                        if (loggedInState) {
                            HomeScreen(
                                onLogout = {
                                    isLoggedIn = false
                                }
                            )
                        } else {
                            AuthScreen(
                                onAuthSuccess = {
                                    isLoggedIn = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
