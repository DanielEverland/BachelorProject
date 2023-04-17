package com.DTU.concussionclient

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SeeSoGazePlayer(
    // Parent activity.
    private val activity : AppCompatActivity,

    // View indicating gaze position.
    private val gazeIndicator : View,

    // Map of timestamps and gaze coordinates.
    private val gazeData : Map<Int, Pair<Float, Float>>,

    // Function to call each time gaze indicator updates.
    private val playbackCallback : (Int) -> (Unit),

    // Function to call when playback ends.
    private val endPlaybackCallback : () -> (Unit)
) {
    private var lastTimestamp = gazeData.keys.last()
    private var playback : Job? = null
    private var playbackProgress : Int = 0
    private var isBlocked = false
    private var resumePlaybackOnUnblock = false

    // Get last timestamp.
    fun getLastTimestamp() : Int {
        return lastTimestamp
    }

    // Get playback progress.
    fun getPlaybackProgress() : Int {
        return playbackProgress
    }

    // Set playback progress and update gaze indicator.
    fun setPlaybackProgress(progress : Int) {
        // Ensure progress is within allowed limits.
        playbackProgress = if (progress < 0) 0
            else if (progress > lastTimestamp) lastTimestamp
            else progress

        // Update gaze indicator.
        displayGazeData(progress)
    }

    fun startPlayback() {
        // If playback is already in progress or is blocked, do nothing.
        if (playback?.isActive == true || isBlocked) {
            return
        }

        // If playback is at the end, restart playback.
        if (playbackProgress == lastTimestamp) playbackProgress = 0

        // Launch playback coroutine.
        playback = activity.lifecycleScope.launch {
            while(playbackProgress < lastTimestamp) {
                // Update gaze indicator.
                displayGazeData(playbackProgress)

                // Launch external playback callback.
                playbackCallback(playbackProgress)

                // Wait for next timestamp in gaze data.
                val nextTimestamp = gazeData.keys.first { k -> k > playbackProgress }
                delay((nextTimestamp - playbackProgress).toLong())

                // Set progress to next timestamp.
                playbackProgress = nextTimestamp
            }

            // Perform gaze indicator update and callback for final timestamp.
            displayGazeData(playbackProgress)
            playbackCallback(playbackProgress)
            endPlaybackCallback()
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

    // Update gaze indicator to match given timestamp.
    private fun displayGazeData(timestamp : Int) {
        // Get coordinates of timestamp.
        var coords = gazeData[timestamp]

        // If no coordinates, find latest timestamp smaller than initial parameter.
        if (coords == null) {
            val closestTimestamp = gazeData.keys.findLast { k -> k < timestamp }
            coords = gazeData[closestTimestamp]
        }

        // Set gaze indicator position.
        if (coords != null) {
            gazeIndicator.translationX = coords.first
            gazeIndicator.translationY = coords.second
        }
    }
}