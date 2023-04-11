package com.DTU.concussionclient

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import camp.visual.gazetracker.device.CameraPosition
import camp.visual.gazetracker.filter.OneEuroFilterManager


class SeeSoActivity : AppCompatActivity() {
    private val TAG = SeeSoActivity::class.java.simpleName
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQ_PERMISSION = 1000
    private lateinit var gazeTracker: GazeTracker

    private lateinit var imageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_so)

        imageView = findViewById(R.id.imageView)

        checkPermission()
        initGaze()
    }

    private fun checkPermission() {
        // Check permission status
        if (!hasPermissions(PERMISSIONS)) {

            requestPermissions(PERMISSIONS, REQ_PERMISSION)
        } else {
            checkPermission(true)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        var result: Int
        // Check permission status in string array
        for (perms in permissions) {
            if (perms == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                if (!Settings.canDrawOverlays(this)) {
                    return false
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms)
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false
            }
        }

        // When if all permission allowed
        return true
    }

    private fun checkPermission(isGranted: Boolean) {
        if (isGranted) {
            permissionGranted()
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PERMISSION -> if (grantResults.size > 0) {
                val cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraPermissionAccepted) {
                    checkPermission(true)
                } else {
                    checkPermission(false)
                }
            }
        }
    }

    private fun permissionGranted() {
        Log.i(TAG, "Permission granted!")
    }

    private fun initGaze() {
        val licenseKey = "dev_bcokijcyjgzfqbwxrcpn4v08jvwlepqfnprihj81"
        GazeTracker.initGazeTracker(applicationContext, licenseKey, initializationCallback)
    }

    private val initializationCallback =
        InitializationCallback { gazeTracker, error ->
            gazeTracker?.let { initSuccess(it) } ?: initFail(error)
        }

    private fun initSuccess(gazeTracker : GazeTracker) {
        this.gazeTracker = gazeTracker
        val cp = CameraPosition("SM-S908B/DS", -37f, 3f)
        gazeTracker.addCameraPosition(cp)
        this.gazeTracker.setGazeCallback(gazeCallback)
        this.gazeTracker.startTracking()
        Log.i("SeeSo", "Initialization success")
    }

    private val oneEuroFilterManager = OneEuroFilterManager(2)

    private val gazeCallback = GazeCallback { gazeInfo ->
        if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            val filteredValues = oneEuroFilterManager.filteredValues
            val filteredX = filteredValues[0]
            val filteredY = filteredValues[1]
            imageView.translationX = filteredX
            imageView.translationY = filteredY
        }
    }

    private fun initFail(error : InitializationErrorType) {
        val err = when (error) {
            // When initialization is failed
            InitializationErrorType.ERROR_INIT ->
                "Initialization failed"

            // When camera permission does not exists
            InitializationErrorType.ERROR_CAMERA_PERMISSION ->
                "Required permission not granted"

            // Gaze library initialization failure
            // It can ba caused by several reasons(i.e. Out of memory).
            else ->
                "init gaze library fail"
        }
        Log.w("SeeSo", "error description: $err")
    }
}