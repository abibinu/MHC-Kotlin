package com.mhc.app.data.model

data class TaskItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val category: String = "Wellness",
    val isCompleted: Boolean = false,
    val iconEmoji: String = "✨"
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false
)
