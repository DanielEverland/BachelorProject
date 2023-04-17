package com.DTU.concussionclient

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import camp.visual.gazetracker.device.CameraPosition
import camp.visual.gazetracker.filter.OneEuroFilterManager

// Initialization and permission requests adapted from SeeSo quick start guide:
// https://docs.seeso.io/nonversioning/quick-start/android-quick-start/
class SeeSoGazeRecorder(
    // Parent activity.
    private val activity : AppCompatActivity,

    // On gaze tracker initialization success callback
    private val initSuccessCallback : () -> (Unit),

    // On gaze tracker initialization failure callback
    private val initFailCallback : (String) -> (Unit)
) {
    // License and permission info.
    private val licenseKey = "dev_bcokijcyjgzfqbwxrcpn4v08jvwlepqfnprihj81"
    private val cameraPermission = Manifest.permission.CAMERA
    private val requestCode = 1000

    // Device info.
    private val modelName = "SM-S908B/DS"
    private val screenOriginX = -37f
    private val screenOriginY = 3f

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

    // On creation, check permission and initialize gaze tracker.
    init {
        checkPermission()
        initGaze()
    }

    // Get gaze tracker data.
    fun getGazeData() : Map<Int, Pair<Float, Float>> {
        return gazeData
    }

    // Start gaze tracking.
    fun startTracking() {
        gazeData = mutableMapOf()
        gazeTracker.startTracking()
    }

    // Stop gaze tracking.
    fun stopTracking() {
        gazeTracker.stopTracking()
    }

    // Check permission status. Request permission if not already granted.
    private fun checkPermission() {
        val result = ContextCompat.checkSelfPermission(activity, cameraPermission)
        if (result == PackageManager.PERMISSION_DENIED) {
            activity.requestPermissions(arrayOf(cameraPermission), requestCode)
        }
    }

    // Initialize gaze tracker.
    private fun initGaze() {
        GazeTracker.initGazeTracker(activity.applicationContext, licenseKey, initializationCallback)
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

            // Get coordinates and timestamps.
            val filteredValues = oneEuroFilterManager.filteredValues
            val filteredX = filteredValues[0]
            val filteredY = filteredValues[1]
            val timestamp = (gazeInfo.timestamp - timestampOffset).toInt()

            // Record gaze tracker data.
            gazeData[timestamp] = Pair(filteredX, filteredY)
        }
    }
}