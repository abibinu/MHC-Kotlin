package com.mhc.app.data.model

import com.google.gson.annotations.SerializedName

data class MoodLogRequest(
    @SerializedName("user_id") val userId: Int,
    val mood: String,
    val note: String? = null
)

data class MoodLogItem(
    @SerializedName("log_id") val logId: Int,
    @SerializedName("user_id") val userId: Int,
    val mood: String,
    val note: String?,
    @SerializedName("logged_at") val loggedAt: String?
)

data class LatestMoodResponse(
    val mood: String?
)
