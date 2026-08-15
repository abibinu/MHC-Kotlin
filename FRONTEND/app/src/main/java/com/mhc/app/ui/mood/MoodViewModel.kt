package com.mhc.app.ui.mood

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mhc.app.data.api.RetrofitClient
import com.mhc.app.data.model.MoodLogItem
import com.mhc.app.data.model.MoodLogRequest
import com.mhc.app.data.session.UserSessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodOption(
    val name: String,
    val emoji: String,
    val colorHex: Long
)

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = UserSessionManager(application)
    val userId = sessionManager.getUserId()

    val availableMoods = listOf(
        MoodOption("Happy", "😄", 0xFFFFD54F),
        MoodOption("Calm", "😌", 0xFF81C784),
        MoodOption("Anxious", "😟", 0xFFFF8A65),
        MoodOption("Sad", "😢", 0xFF64B5F6),
        MoodOption("Angry", "😠", 0xFFE57373),
        MoodOption("Energetic", "⚡", 0xFFBA68C8)
    )

    var selectedMood by mutableStateOf<MoodOption?>(availableMoods[0])
    var noteInput by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val moodHistory = mutableStateListOf<MoodLogItem>()
    var analyticsMap by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    init {
        loadMoodData()
    }

    fun selectMood(option: MoodOption) {
        selectedMood = option
    }

    fun loadMoodData() {
        if (userId > 0) {
            fetchMoodHistory()
            fetchAnalytics()
        } else {
            loadDefaultOfflineData()
        }
    }

    fun logMood() {
        val moodName = selectedMood?.name ?: return
        val note = noteInput.trim()

        isLoading = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addMoodLog(
                    MoodLogRequest(
                        userId = if (userId > 0) userId else 999,
                        mood = moodName,
                        note = if (note.isNotBlank()) note else null
                    )
                )

                isLoading = false
                if (response.isSuccessful) {
                    successMessage = "Mood '$moodName' logged successfully!"
                    noteInput = ""
                    fetchMoodHistory()
                    fetchAnalytics()
                } else {
                    addOfflineMoodLog(moodName, note)
                }
            } catch (e: Exception) {
                isLoading = false
                addOfflineMoodLog(moodName, note)
            }
        }
    }

    private fun addOfflineMoodLog(moodName: String, note: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val newLog = MoodLogItem(
            logId = (moodHistory.size + 1),
            userId = if (userId > 0) userId else 999,
            mood = moodName,
            note = if (note.isNotBlank()) note else null,
            loggedAt = dateFormat.format(Date())
        )
        moodHistory.add(0, newLog)

        val updatedMap = analyticsMap.toMutableMap()
        updatedMap[moodName] = (updatedMap[moodName] ?: 0) + 1
        analyticsMap = updatedMap

        successMessage = "Mood '$moodName' logged locally!"
        noteInput = ""
    }

    private fun fetchMoodHistory() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMoodLogs(userId)
                if (response.isSuccessful && response.body() != null) {
                    moodHistory.clear()
                    moodHistory.addAll(response.body()!!)
                }
            } catch (e: Exception) {
                // Keep local logs if offline
            }
        }
    }

    private fun fetchAnalytics() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMoodAnalytics(userId)
                if (response.isSuccessful && response.body() != null) {
                    analyticsMap = response.body()!!
                }
            } catch (e: Exception) {
                // Fallback to local count calculation
                recalculateLocalAnalytics()
            }
        }
    }

    private fun recalculateLocalAnalytics() {
        val map = mutableMapOf<String, Int>()
        for (item in moodHistory) {
            map[item.mood] = (map[item.mood] ?: 0) + 1
        }
        analyticsMap = map
    }

    private fun loadDefaultOfflineData() {
        if (moodHistory.isEmpty()) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val now = System.currentTimeMillis()
            moodHistory.addAll(
                listOf(
                    MoodLogItem(1, 999, "Happy", "Felt peaceful after morning walk", dateFormat.format(Date(now - 3600000 * 2))),
                    MoodLogItem(2, 999, "Calm", "Meditation session went great", dateFormat.format(Date(now - 3600000 * 24))),
                    MoodLogItem(3, 999, "Anxious", "Work deadline approaching", dateFormat.format(Date(now - 3600000 * 48)))
                )
            )
            recalculateLocalAnalytics()
        }
    }
}
