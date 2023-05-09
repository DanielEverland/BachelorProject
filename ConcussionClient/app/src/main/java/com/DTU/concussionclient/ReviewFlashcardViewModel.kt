package com.DTU.concussionclient

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SeeSoUiState(
    val isPlaying : Boolean = false,
    val playProgress : Int = 0,
    val maxProgress : Int? = null,
    val indicatorX : Float? = null,
    val indicatorY : Float? = null
)

class ReviewFlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private var audioGazePlayer : AudioGazePlayer? = null
    private val _uiState = MutableStateFlow(SeeSoUiState())

    val uiState : StateFlow<SeeSoUiState> = _uiState.asStateFlow()

    fun initGazePlayer() {
        val concussionApplication = getApplication<ConcussionApplication>()
        val gazeRecorder = concussionApplication.gazeRecorder
        val gazeData = gazeRecorder?.getGazeData()
        val audioPlayer = MediaPlayer()
        audioPlayer.setDataSource(concussionApplication.audioFilePath)
        audioPlayer.prepare()

        audioGazePlayer = AudioGazePlayer(this, gazeData, audioPlayer)

        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = false,
                playProgress = 0,
                maxProgress = audioPlayer.duration,
                indicatorX = null,
                indicatorY = null
            )
        }
    }

    fun togglePlay() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = !currentState.isPlaying
            )
        }

        if (uiState.value.isPlaying) {
            audioGazePlayer?.startPlayback()
        }
        else {
            audioGazePlayer?.pausePlayback()
        }
    }

    fun blockPlayback() {
        audioGazePlayer?.blockPlayback()
    }

    fun unblockPlayback() {
        audioGazePlayer?.unblockPlayback()
    }

    fun stopPlayback() {
        audioGazePlayer?.stopPlayback()
    }

    fun seekTo(timestamp : Int) {
        audioGazePlayer?.seekTo(timestamp)
    }

    fun updateForProgress(progress : Int) {
        val coords = audioGazePlayer?.getCoords(progress)
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = if (progress < (currentState.maxProgress ?: 0)) currentState.isPlaying else false,
                playProgress = progress,
                indicatorX = coords?.first,
                indicatorY = coords?.second
            )
        }
    }

    fun getTimestampText(timestamp : Int) : String {
        return (timestamp / 60000).toString() +
                ":" +
                (timestamp / 1000).toString().padStart(2, '0')
    }
}