package com.DTU.concussionclient

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SeeSoGazePlayer(
    // Parent view model.
    private val viewModel : SeeSoViewModel,

    // Map of timestamps and gaze coordinates.
    private val gazeData : Map<Int, Pair<Float, Float>>
) {
    private var lastTimestamp = gazeData.keys.last()
    private var playback : Job? = null
    private var playbackProgress : Int = 0
    private var isBlocked = false
    private var resumePlaybackOnUnblock = false

    init {
        viewModel.setPlaybackProgress(playbackProgress)
    }

    // Retrieve latest gaze data for the given timestamp.
    fun retrieveGazeData(timestamp: Int) : Pair<Float, Float> {
        // Get valid timestamp
        val validTimestamp = getValidTimestamp(timestamp)

        // Get coordinates of timestamp.
        var coords = gazeData[validTimestamp]

        // If no coordinates, find latest timestamp smaller than initial parameter.
        if (coords == null) {
            val closestTimestamp = gazeData.keys.findLast { k -> k < validTimestamp } ?: 0
            coords = gazeData.getValue(closestTimestamp)
        }

        return Pair(coords.first, coords.second)
    }

    // Start playback.
    fun startPlayback() {
        // If playback is already in progress or is blocked, do nothing.
        if (playback?.isActive == true || isBlocked) {
            return
        }

        // If playback is at the end, restart playback.
        if (playbackProgress == lastTimestamp) playbackProgress = 0

        // Launch playback coroutine.
        playback = viewModel.viewModelScope.launch {
            while(playbackProgress < lastTimestamp) {
                // Update view model.
                viewModel.setPlaybackProgress(playbackProgress)

                // Wait for next timestamp in gaze data.
                val nextTimestamp = gazeData.keys.first { k -> k > playbackProgress }
                delay((nextTimestamp - playbackProgress).toLong())

                // Set progress to next timestamp.
                playbackProgress = nextTimestamp
            }

            // Perform final view model update.
            viewModel.setPlaybackProgress(playbackProgress)
            viewModel.pausePlayback()
        }
    }

    // Pause playback.
    fun pausePlayback() {
        if (playback?.isActive == true) {
            playback?.cancel()
        }
    }

    // Pause and prevent playback until unblocked.
    fun blockPlayback() {
        if (playback?.isActive == true) {
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

    // Adjust a given timestamp to be within allowed limits.
    private fun getValidTimestamp(timestamp : Int) : Int {
        return if (timestamp < 0) 0
        else if (timestamp > lastTimestamp) lastTimestamp
        else timestamp
    }
}