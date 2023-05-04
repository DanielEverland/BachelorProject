package com.DTU.concussionclient

import android.app.Application
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

class SeeSoViewModel(application: Application) : AndroidViewModel(application) {
    private var gazePlayer : SeeSoGazePlayer? = null
    private val _uiState = MutableStateFlow(SeeSoUiState())

    val uiState : StateFlow<SeeSoUiState> = _uiState.asStateFlow()

    fun initGazePlayer(gazeData : Map<Int, Pair<Float, Float>>?) {
        if (!gazeData.isNullOrEmpty()) {
            gazePlayer = SeeSoGazePlayer(this, gazeData)

            _uiState.update { currentState ->
                currentState.copy(
                    isPlaying = false,
                    playProgress = 0,
                    maxProgress = gazeData.keys.last(),
                    indicatorX = gazeData[0]?.first,
                    indicatorY = gazeData[0]?.second
                )
            }
        }
    }

    fun togglePlay() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = !currentState.isPlaying
            )
        }

        if (uiState.value.isPlaying) {
            gazePlayer?.startPlayback()
        }
        else {
            gazePlayer?.pausePlayback()
        }
    }

    fun startPlayback() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = true
            )
        }
        gazePlayer?.startPlayback()
    }

    fun pausePlayback() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = false
            )
        }
        gazePlayer?.pausePlayback()
    }

    fun blockPlayback() {
        gazePlayer?.blockPlayback()
    }

    fun unblockPlayback() {
        gazePlayer?.unblockPlayback()
    }

    fun setPlaybackProgress(progress : Int) {
        val coords = gazePlayer?.setTimestamp(progress)
        _uiState.update { currentState ->
            currentState.copy(
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