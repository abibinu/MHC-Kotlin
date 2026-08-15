package com.mhc.app.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isPending: Boolean = false
)

data class ChatRequest(
    @SerializedName("user_id") val userId: Int,
    val message: String,
    val language: String = "English",
    @SerializedName("user_name") val userName: String = "User"
)

data class ChatResponse(
    val reply: String?
)

data class ChatHistoryItem(
    @SerializedName("log_id") val logId: Int,
    @SerializedName("user_id") val userId: Int,
    val message: String,
    val response: String,
    @SerializedName("created_at") val createdAt: String?
)

data class ChatHistoryResponse(
    val history: List<ChatHistoryItem>?
)
