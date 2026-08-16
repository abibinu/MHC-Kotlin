package com.mhc.app.ui.tasks

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mhc.app.data.model.AchievementBadge
import com.mhc.app.data.model.TaskItem

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mhc_tasks_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    val tasks = mutableStateListOf<TaskItem>()
    val achievements = mutableStateListOf<AchievementBadge>()

    var newTaskTitle by mutableStateOf("")

    init {
        loadTasks()
        updateAchievements()
    }

    private fun loadTasks() {
        val savedJson = prefs.getString("user_tasks_json", null)
        if (!savedJson.isNull_blank()) {
            try {
                val type = object : TypeToken<List<TaskItem>>() {}.type
                val savedList: List<TaskItem> = gson.fromJson(savedJson, type)
                tasks.clear()
                tasks.addAll(savedList)
            } catch (e: Exception) {
                loadDefaultTasks()
            }
        } else {
            loadDefaultTasks()
        }
    }

    private fun String?.isNull_blank(): Boolean = this == null || this.trim().isEmpty()

    private fun loadDefaultTasks() {
        tasks.clear()
        tasks.addAll(
            listOf(
                TaskItem(title = "Drink 2 Liters of Water", category = "Hydration", iconEmoji = "💧"),
                TaskItem(title = "Take a 15-Min Mindful Walk", category = "Exercise", iconEmoji = "🚶"),
                TaskItem(title = "Write Down 3 Gratitudes", category = "Mindfulness", iconEmoji = "✍️"),
                TaskItem(title = "5-Minute Deep Breathing", category = "Relaxation", iconEmoji = "🧘"),
                TaskItem(title = "1-Hour Digital Screen Detox", category = "Rest", iconEmoji = "📱"),
                TaskItem(title = "8 Hours Quality Sleep", category = "Health", iconEmoji = "😴")
            )
        )
        saveTasks()
    }

    private fun saveTasks() {
        val json = gson.toJson(tasks.toList())
        prefs.edit().putString("user_tasks_json", json).apply()
    }

    fun toggleTask(taskId: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val item = tasks[index]
            tasks[index] = item.copy(isCompleted = !item.isCompleted)
            saveTasks()
            updateAchievements()
        }
    }

    fun addTask() {
        val title = newTaskTitle.trim()
        if (title.isNotBlank()) {
            tasks.add(TaskItem(title = title, category = "Custom", iconEmoji = "⭐"))
            newTaskTitle = ""
            saveTasks()
            updateAchievements()
        }
    }

    fun deleteTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
        saveTasks()
        updateAchievements()
    }

    fun resetProgress() {
        for (i in tasks.indices) {
            tasks[i] = tasks[i].copy(isCompleted = false)
        }
        saveTasks()
        updateAchievements()
    }

    fun getCompletionPercentage(): Float {
        if (tasks.isEmpty()) return 0.0f
        val completed = tasks.count { it.isCompleted }
        return completed.toFloat() / tasks.size.toFloat()
    }

    private fun updateAchievements() {
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val percentage = getCompletionPercentage()

        achievements.clear()
        achievements.addAll(
            listOf(
                AchievementBadge(
                    id = "ach_1",
                    title = "First Step",
                    description = "Completed 1 wellness micro-habit",
                    emoji = "🌟",
                    isUnlocked = completed >= 1
                ),
                AchievementBadge(
                    id = "ach_2",
                    title = "Halfway There",
                    description = "Completed at least 50% of daily habits",
                    emoji = "🎯",
                    isUnlocked = percentage >= 0.5f && total > 0
                ),
                AchievementBadge(
                    id = "ach_3",
                    title = "Goal Master",
                    description = "Completed 100% of today's checklist",
                    emoji = "🏆",
                    isUnlocked = percentage >= 1.0f && total > 0
                ),
                AchievementBadge(
                    id = "ach_4",
                    title = "Mindfulness Star",
                    description = "Added custom wellness micro-habits",
                    emoji = "✨",
                    isUnlocked = tasks.any { it.category == "Custom" }
                )
            )
        )
    }
}
