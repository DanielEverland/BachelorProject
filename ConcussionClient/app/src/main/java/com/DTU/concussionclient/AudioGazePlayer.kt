package com.DTU.concussionclient

import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioGazePlayer(
    // Parent view model.
    private val viewModel : ReviewFlashcardViewModel,

    // Map of timestamps and gaze coordinates.
    private val gazeData : Map<Int, Pair<Float, Float>>,

    // Media player for voice recording.
    private val audioPlayer : MediaPlayer
) {
    private val updateDelay = 33
    private var maxProgress = audioPlayer.duration
    private var playback : Job? = null
    private var progress : Int = 0
    private var isBlocked = false
    private var resumePlaybackOnUnblock = false

    // Get coordinates for timestamp.
    fun getCoords(timestamp: Int) : Pair<Float, Float>? {
        // Get valid timestamp.
        val validTimestamp = getValidTimestamp(timestamp)

        // Get coordinates of timestamp.
        var coords = gazeData[validTimestamp]

        // If no coordinates, try to find latest timestamp within update delay.
        if (coords == null) {
            val closestTimestamp = gazeData.keys.findLast { k -> k < validTimestamp - updateDelay } ?: 0
            coords = gazeData[closestTimestamp]
        }

        return coords
    }

    // Set to given timestamp.
    fun seekTo(timestamp: Int) {
        // Get and set valid timestamp.
        val validTimestamp = getValidTimestamp(timestamp)
        progress = validTimestamp

        // Set audio player progress.
        audioPlayer.seekTo(validTimestamp)

        viewModel.updateForProgress(validTimestamp)
    }

    // Start playback.
    fun startPlayback() {
        // If playback is already in progress or is blocked, do nothing.
        if (playback?.isActive == true || isBlocked) {
            return
        }

        // Start audio player and update progress.
        audioPlayer.start()
        progress = audioPlayer.currentPosition

        // Launch playback coroutine.
        playback = viewModel.viewModelScope.launch {
            while(progress < maxProgress) {
                // Update view model.
                viewModel.updateForProgress(progress)

                // Wait for specified delay.
                delay(updateDelay.toLong())

                // Update progress.
                progress = audioPlayer.currentPosition
            }

            // Perform final view model update.
            viewModel.updateForProgress(progress)
        }
    }

    // Pause playback.
    fun pausePlayback() {
        if (playback?.isActive == true) {
            audioPlayer.pause()
            playback?.cancel()
        }
    }

    // Pause and prevent playback until unblocked.
    fun blockPlayback() {
        if (playback?.isActive == true) {
            audioPlayer.pause()
            playback?.cancel()
            resumePlaybackOnUnblock = true
        }
        isBlocked = true
    }

    // Unblock playback.
    fun unblockPlayback() {
        isBlocked = false

        // If playback was underway when blocked, resume playback.
        if (resumePlaybackOnUnblock) {
            startPlayback()
            resumePlaybackOnUnblock = false
        }
    }

    // Stop playback.
    fun stopPlayback() {
        if (playback?.isActive == true) {
            playback?.cancel()
        }
        audioPlayer.stop()
    }

    // Adjust a given timestamp to be within allowed limits.
    private fun getValidTimestamp(timestamp : Int) : Int {
        return if (timestamp < 0) 0
        else if (timestamp > maxProgress) maxProgress
        else timestamp
    }
}