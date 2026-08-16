package com.mhc.app.data.model

data class SoundTrack(
    val id: String,
    val title: String,
    val category: String,
    val artist: String = "Calm Soundscapes",
    val durationSeconds: Int,
    val streamUrl: String,
    val iconEmoji: String,
    val colorHex: Long
)
