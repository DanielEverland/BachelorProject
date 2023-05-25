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
    private val licenseKey = "dev_ny2wjyiekw3tgv8ojr5k0r4034tuksamith6su68"

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
    private var gazeTracker : GazeTracker? = null
    private val oneEuroFilterManager = OneEuroFilterManager(2)
    private lateinit var gazeData : MutableMap<Int, Pair<Float, Float>>
    private var timestampOffset = 0L

    private val errorDescriptions = mutableMapOf<InitializationErrorType, String>(
        Pair(InitializationErrorType.ERROR_NONE, "ERROR_NONE: authentication is succeeded."),
        Pair(InitializationErrorType.ERROR_INIT, "ERROR_INIT: failed initialization."),
        Pair(InitializationErrorType.ERROR_CAMERA_PERMISSION, "ERROR_CAMERA_PERMISSION: failed to get camera permission."),
        Pair(InitializationErrorType.AUTH_INVALID_KEY, "AUTH_INVALID_KEY: the license key is invalid."),
        Pair(InitializationErrorType.AUTH_INVALID_ENV_USED_DEV_IN_PROD, "AUTH_INVALID_ENV_USED_DEV_IN_PROD: trying to use dev license key in prod environment."),
        Pair(InitializationErrorType.AUTH_INVALID_ENV_USED_PROD_IN_DEV, "AUTH_INVALID_ENV_USED_PROD_IN_DEV: trying to use prod license key in dev environment."),
        Pair(InitializationErrorType.AUTH_INVALID_PACKAGE_NAME, "AUTH_INVALID_PACKAGE_NAME: using wrong package name."),
        Pair(InitializationErrorType.AUTH_INVALID_APP_SIGNATURE, "AUTH_INVALID_APP_SIGNATURE: using wrong application signature."),
        Pair(InitializationErrorType.AUTH_EXCEEDED_FREE_TIER, "AUTH_EXCEEDED_FREE_TIER: the free usage limit is exceeded."),
        Pair(InitializationErrorType.AUTH_DEACTIVATED_KEY, "AUTH_DEACTIVATED_KEY: trying to use deactivated license key."),
        Pair(InitializationErrorType.AUTH_INVALID_ACCESS, "AUTH_INVALID_ACCESS: using invalid access method."),
        Pair(InitializationErrorType.AUTH_UNKNOWN_ERROR, "AUTH_UNKNOWN_ERROR: unknown error from the host server."),
        Pair(InitializationErrorType.AUTH_SERVER_ERROR, "AUTH_SERVER_ERROR: internal error from the host server."),
        Pair(InitializationErrorType.AUTH_CANNOT_FIND_HOST, "AUTH_CANNOT_FIND_HOST: lost connection or using wrong host address."),
        Pair(InitializationErrorType.AUTH_WRONG_LOCAL_TIME, "AUTH_WRONG_LOCAL_TIME: there is a gap between the device time and the server time."),
        Pair(InitializationErrorType.AUTH_INVALID_KEY_FORMAT, "AUTH_INVALID_KEY_FORMAT: using wrong license key format."),
        Pair(InitializationErrorType.AUTH_EXPIRE_KEY, "AUTH_EXPIRE_KEY: using expired license key."),
        Pair(InitializationErrorType.ERROR_NOT_ADVANCED_TIER, "ERROR_NOT_ADVANCED_TIER: trying to use User Status Detector with basic production license key.")
    )

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
        timestampOffset = System.currentTimeMillis()
        gazeTracker?.startTracking()
    }

    // Stop gaze tracking.
    fun stopTracking() {
        gazeTracker?.stopTracking()
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
        this.gazeTracker?.addCameraPosition(cp)

        // Configure gaze tracker callback.
        this.gazeTracker?.setGazeCallback(gazeCallback)

        // Launch external init success callback.
        initSuccessCallback()
    }

    // On gaze tracker initialization failure.
    private fun initFail(error : InitializationErrorType) {
        // Determine error.
        val errorDescription = errorDescriptions[error] ?: "Unknown error occurred."

        // Launch external init fail callback.
        initFailCallback(errorDescription)
    }

    // On gaze tracker callback.
    private val gazeCallback = GazeCallback { gazeInfo ->
        if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
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