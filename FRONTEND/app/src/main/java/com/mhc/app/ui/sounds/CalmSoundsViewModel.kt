package com.mhc.app.ui.sounds

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mhc.app.data.model.SoundTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CalmSoundsViewModel(application: Application) : AndroidViewModel(application) {

    val allTracks = listOf(
        SoundTrack(
            id = "t1",
            title = "Gentle Rain",
            category = "Nature",
            artist = "Nature Soundscapes",
            durationSeconds = 180,
            streamUrl = "https://cdn.pixabay.com/download/audio/2021/09/06/audio_145b23d913.mp3",
            iconEmoji = "🌧️",
            colorHex = 0xFF4FC3F7
        ),
        SoundTrack(
            id = "t2",
            title = "Ocean Waves",
            category = "Nature",
            artist = "Nature Soundscapes",
            durationSeconds = 210,
            streamUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3",
            iconEmoji = "🌊",
            colorHex = 0xFF0288D1
        ),
        SoundTrack(
            id = "t3",
            title = "Calm Forest & Birds",
            category = "Nature",
            artist = "Nature Soundscapes",
            durationSeconds = 240,
            streamUrl = "https://cdn.pixabay.com/download/audio/2022/03/10/audio_c8c8a73229.mp3",
            iconEmoji = "🌲",
            colorHex = 0xFF66BB6A
        ),
        SoundTrack(
            id = "t4",
            title = "Soft Piano Harmony",
            category = "Acoustic",
            artist = "Relaxation Melodies",
            durationSeconds = 195,
            streamUrl = "https://cdn.pixabay.com/download/audio/2022/01/18/audio_d0a13f69d2.mp3",
            iconEmoji = "🎹",
            colorHex = 0xFFAB47BC
        ),
        SoundTrack(
            id = "t5",
            title = "Meditation Chimes",
            category = "Acoustic",
            artist = "Relaxation Melodies",
            durationSeconds = 220,
            streamUrl = "https://cdn.pixabay.com/download/audio/2022/03/15/audio_c299c15d48.mp3",
            iconEmoji = "🔔",
            colorHex = 0xFFFF7043
        ),
        SoundTrack(
            id = "t6",
            title = "Deep Focus Ambience",
            category = "Focus",
            artist = "Mindful Frequencies",
            durationSeconds = 300,
            streamUrl = "https://cdn.pixabay.com/download/audio/2022/10/14/audio_9939f7336f.mp3",
            iconEmoji = "🎧",
            colorHex = 0xFF26A69A
        )
    )

    var selectedCategory by mutableStateOf("All")
        private set

    var currentTrackIndex by mutableIntStateOf(0)
        private set

    val currentTrack: SoundTrack
        get() = filteredTracks.getOrElse(currentTrackIndex) { allTracks[0] }

    var isPlaying by mutableStateOf(false)

    var currentPositionMs by mutableIntStateOf(0)

    var durationMs by mutableIntStateOf(180000)

    var isBuffering by mutableStateOf(false)

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    val filteredTracks: List<SoundTrack>
        get() = if (selectedCategory == "All") allTracks else allTracks.filter { it.category == selectedCategory }

    fun selectCategory(category: String) {
        selectedCategory = category
        currentTrackIndex = 0
    }

    fun playTrack(track: SoundTrack) {
        val index = filteredTracks.indexOfFirst { it.id == track.id }
        if (index != -1) {
            currentTrackIndex = index
        }
        startPlayback(track)
    }

    fun togglePlayPause() {
        if (mediaPlayer == null) {
            startPlayback(currentTrack)
        } else {
            if (isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
            } else {
                mediaPlayer?.start()
                isPlaying = true
                startProgressTracker()
            }
        }
    }

    fun playNext() {
        if (filteredTracks.isNotEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % filteredTracks.size
            startPlayback(currentTrack)
        }
    }

    fun playPrevious() {
        if (filteredTracks.isNotEmpty()) {
            currentTrackIndex = if (currentTrackIndex - 1 < 0) filteredTracks.size - 1 else currentTrackIndex - 1
            startPlayback(currentTrack)
        }
    }

    fun seekTo(positionMs: Float) {
        val targetMs = positionMs.toInt()
        currentPositionMs = targetMs
        mediaPlayer?.seekTo(targetMs)
    }

    private fun startPlayback(track: SoundTrack) {
        try {
            stopPlayback()
            isBuffering = true
            isPlaying = false

            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(track.streamUrl)
            player.setOnPreparedListener { mp ->
                this@CalmSoundsViewModel.isBuffering = false
                this@CalmSoundsViewModel.durationMs = if (mp.duration > 0) mp.duration else track.durationSeconds * 1000
                mp.start()
                this@CalmSoundsViewModel.isPlaying = true
                this@CalmSoundsViewModel.startProgressTracker()
            }
            player.setOnCompletionListener {
                this@CalmSoundsViewModel.playNext()
            }
            player.setOnErrorListener { _, _, _ ->
                this@CalmSoundsViewModel.isBuffering = false
                this@CalmSoundsViewModel.isPlaying = false
                true
            }
            player.prepareAsync()
            mediaPlayer = player
        } catch (e: Exception) {
            isBuffering = false
            isPlaying = false
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && isPlaying) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        currentPositionMs = mp.currentPosition
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopPlayback() {
        progressJob?.cancel()
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
