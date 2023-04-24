package com.DTU.concussionclient

import android.content.Context
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import camp.visual.gazetracker.device.CameraPosition
import camp.visual.gazetracker.filter.OneEuroFilterManager

class SeeSoGazeRecorder(
    // Application context.
    private val appContext : Context,

    // On gaze tracker initialization success callback
    private val initSuccessCallback : () -> (Unit),

    // On gaze tracker initialization failure callback
    private val initFailCallback : (String) -> (Unit)
) {
    // License info.
    private val licenseKey = "dev_bcokijcyjgzfqbwxrcpn4v08jvwlepqfnprihj81"

    // Device info.
    private val modelName = "SM-S908B/DS"
    private val screenOriginX = -37f
    private val screenOriginY = 3f

    // Flashcard dimensions.
    private var flashcardWidth : Int? = null
    private var flashcardHeight : Int? = null
    private var flashcardXOffset : Int? = null
    private var flashcardYOffset : Int? = null

    // Gaze tracker fields.
    private lateinit var gazeTracker : GazeTracker
    private val oneEuroFilterManager = OneEuroFilterManager(2)
    private lateinit var gazeData : MutableMap<Int, Pair<Float, Float>>
    private var timestampOffset = 0L

    // On gaze tracker initialization.
    private val initializationCallback =
        InitializationCallback { gazeTracker, error ->
            gazeTracker?.let { initSuccess(it) } ?: initFail(error)
        }

    init {
        initGaze()
    }

    // Get gaze tracker data.
    fun getGazeData() : Map<Int, Pair<Float, Float>> {
        return gazeData
    }

    // Start gaze tracking.
    fun startTracking(width : Int, height : Int, xOffset : Int, yOffset : Int) {
        flashcardWidth = width
        flashcardHeight = height
        flashcardXOffset = xOffset
        flashcardYOffset = yOffset
        gazeData = mutableMapOf()
        gazeTracker.startTracking()
    }

    // Stop gaze tracking.
    fun stopTracking() {
        gazeTracker.stopTracking()
    }

    // Initialize gaze tracker.
    private fun initGaze() {
        GazeTracker.initGazeTracker(appContext, licenseKey, initializationCallback)
    }

    // On gaze tracker initialization success.
    private fun initSuccess(gazeTracker : GazeTracker) {
        this.gazeTracker = gazeTracker

        // Set camera position.
        val cp = CameraPosition(modelName, screenOriginX, screenOriginY)
        gazeTracker.addCameraPosition(cp)

        // Configure gaze tracker callback.
        this.gazeTracker.setGazeCallback(gazeCallback)

        // Launch external init success callback.
        initSuccessCallback()
    }

    // On gaze tracker initialization failure.
    private fun initFail(error : InitializationErrorType) {
        // Determine error.
        val err = when (error) {
            // When initialization is failed.
            InitializationErrorType.ERROR_INIT ->
                "Initialization failed"

            // When camera permission does not exists.
            InitializationErrorType.ERROR_CAMERA_PERMISSION ->
                "Required permission not granted"

            // Gaze library initialization failure.
            // It can ba caused by several reasons(i.e. Out of memory).
            else ->
                "init gaze library fail"
        }

        // Launch external init fail callback.
        initFailCallback(err)
    }

    // On gaze tracker callback.
    private val gazeCallback = GazeCallback { gazeInfo ->
        if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            // If no gaze data, use first timestamp as offset to start at 0.
            if (gazeData.isEmpty()) {
                timestampOffset = gazeInfo.timestamp
            }

            // Get and convert coordinates and timestamps.
            val filteredValues = oneEuroFilterManager.filteredValues
            val x = (filteredValues[0] - checkNotNull(flashcardXOffset)) / checkNotNull(flashcardWidth)
            val y = (filteredValues[1] - checkNotNull(flashcardYOffset)) / checkNotNull(flashcardHeight)
            val timestamp = (gazeInfo.timestamp - timestampOffset).toInt()

            // Record gaze tracker data.
            gazeData[timestamp] = Pair(x, y)
        }
    }
}