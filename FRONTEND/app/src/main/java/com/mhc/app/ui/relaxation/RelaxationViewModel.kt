package com.mhc.app.ui.relaxation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class BreathingPhase {
    IDLE,
    INHALE,
    HOLD,
    EXHALE
}

class RelaxationViewModel(application: Application) : AndroidViewModel(application) {

    var currentPhase by mutableStateOf(BreathingPhase.IDLE)
        private set

    var circleScale by mutableFloatStateOf(0.6f)
        private set

    var secondsRemaining by mutableIntStateOf(3)
        private set

    var completedCycles by mutableIntStateOf(0)
        private set

    var tapScore by mutableIntStateOf(0)
        private set

    var isSessionActive by mutableStateOf(false)
        private set

    val tipsList = listOf(
        "Focus gently on the expansion of your chest.",
        "Feel your shoulders lower as you release air.",
        "Softly unclench your jaw and rest your tongue.",
        "Allow every exhale to carry away physical tension.",
        "You are safe, calm, and present in this moment."
    )

    var currentTip by mutableStateOf(tipsList[0])
        private set

    private var timerJob: Job? = null

    fun startOrPauseSession() {
        if (isSessionActive) {
            pauseSession()
        } else {
            startSession()
        }
    }

    private fun startSession() {
        isSessionActive = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && isSessionActive) {
                // Phase 1: INHALE (3 seconds)
                currentPhase = BreathingPhase.INHALE
                secondsRemaining = 3
                val inhaleSteps = 30
                for (i in 1..inhaleSteps) {
                    if (!isSessionActive) break
                    circleScale = 0.6f + (0.65f * (i / inhaleSteps.toFloat()))
                    delay(100)
                }

                if (!isSessionActive) break

                // Phase 2: HOLD (2 seconds)
                currentPhase = BreathingPhase.HOLD
                secondsRemaining = 2
                circleScale = 1.25f
                for (i in 1..20) {
                    if (!isSessionActive) break
                    delay(100)
                }

                if (!isSessionActive) break

                // Phase 3: EXHALE (3 seconds)
                currentPhase = BreathingPhase.EXHALE
                secondsRemaining = 3
                val exhaleSteps = 30
                for (i in 1..exhaleSteps) {
                    if (!isSessionActive) break
                    circleScale = 1.25f - (0.65f * (i / exhaleSteps.toFloat()))
                    delay(100)
                }

                if (isSessionActive) {
                    completedCycles++
                    currentTip = tipsList[(completedCycles) % tipsList.size]
                }
            }
        }
    }

    fun pauseSession() {
        isSessionActive = false
        currentPhase = BreathingPhase.IDLE
        circleScale = 0.6f
        timerJob?.cancel()
    }

    fun resetSession() {
        pauseSession()
        completedCycles = 0
        tapScore = 0
    }

    fun onTapBeat() {
        if (isSessionActive) {
            tapScore += 10
        }
    }
}
