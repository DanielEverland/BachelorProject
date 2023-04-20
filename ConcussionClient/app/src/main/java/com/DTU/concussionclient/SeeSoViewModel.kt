package com.DTU.concussionclient

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SeeSoUiState(
    val enableRecord : Boolean = false,
    val enablePlay : Boolean = false,
    val playProgress : Int = 0,
    val maxProgress : Int? = null,
    val indicatorX : Float? = null,
    val indicatorY : Float? = null
)

class SeeSoViewModel(application: Application) : AndroidViewModel(application) {
    private val gazeRecorder : SeeSoGazeRecorder = SeeSoGazeRecorder(
        getApplication<Application>().applicationContext,
        ::initSuccess,
        ::initFail)
    private var gazePlayer : SeeSoGazePlayer? = null
    private val _uiState = MutableStateFlow(SeeSoUiState())

    val uiState : StateFlow<SeeSoUiState> = _uiState.asStateFlow()

    fun startRecording() {
        _uiState.update { currentState ->
            currentState.copy(
                enableRecord = true,
                enablePlay = false,
                playProgress = 0,
                maxProgress = null,
                indicatorX = null,
                indicatorY = null
            )
        }
        gazeRecorder.startTracking()
    }

    fun stopRecording() {
        gazeRecorder.stopTracking()
        val gazeData = gazeRecorder.getGazeData()
        val hasData = gazeData.isNotEmpty()
        gazePlayer = if (hasData) SeeSoGazePlayer(this, gazeData) else null
        _uiState.update { currentState ->
            currentState.copy(
                enableRecord = true,
                enablePlay = hasData,
                playProgress = 0,
                maxProgress = gazeData.keys.last(),
                indicatorX = gazeData[0]?.first,
                indicatorY = gazeData[0]?.second
            )
        }
    }

    fun startPlayback() {
        _uiState.update { currentState ->
            currentState.copy(
                enableRecord = false
            )
        }
        gazePlayer?.startPlayback()
    }

    fun pausePlayback() {
        _uiState.update { currentState ->
            currentState.copy(
                enableRecord = true
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
        val coords = gazePlayer?.retrieveGazeData(progress)
        _uiState.update { currentState ->
            currentState.copy(
                playProgress = progress,
                indicatorX = coords?.first,
                indicatorY = coords?.second
            )
        }
    }

    private fun initSuccess() {
        _uiState.update { currentState ->
            currentState.copy(
                enableRecord = true
            )
        }
    }

    private fun initFail(error : String) {
        Log.w("SeeSo", "error description: $error")
    }
}