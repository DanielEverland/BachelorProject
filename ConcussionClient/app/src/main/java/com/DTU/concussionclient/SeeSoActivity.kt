package com.DTU.concussionclient

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import camp.visual.gazetracker.device.CameraPosition
import camp.visual.gazetracker.filter.OneEuroFilterManager
import kotlinx.coroutines.*


class SeeSoActivity : AppCompatActivity() {
    private val TAG = SeeSoActivity::class.java.simpleName
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQ_PERMISSION = 1000
    private lateinit var gazeTracker: GazeTracker
    private lateinit var gazeData : MutableMap<Long, Pair<Float, Float>>
    private var lastTimestamp = 0L
    private lateinit var playback : Job
    private var playProgress : Long = 0
    private var isPlaying = false

    private lateinit var toggleButtonRecord : ToggleButton
    private lateinit var toggleButtonPlay : ToggleButton
    private lateinit var seekBar : SeekBar
    private lateinit var imageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_so)

        toggleButtonRecord = findViewById(R.id.toggleButtonRecord)
        toggleButtonPlay = findViewById(R.id.toggleButtonPlay)
        seekBar = findViewById(R.id.seekBar)
        imageView = findViewById(R.id.imageView)

        enableRecord(false)
        enablePlay(false)

        checkPermission()
        initGaze()

        toggleButtonRecord.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enablePlay(false)
                gazeData = mutableMapOf()
                gazeTracker.startTracking()
            }
            else {
                gazeTracker.stopTracking()

                if (gazeData.isNotEmpty()) {
                    // Modify timestamps to begin at 0
                    val firstTimeStamp = gazeData.keys.first()
                    gazeData = gazeData.mapKeys { it.key - firstTimeStamp} as MutableMap<Long, Pair<Float, Float>>

                    // Set limit of seek bar
                    lastTimestamp = gazeData.keys.last()
                    seekBar.max = lastTimestamp.toInt()

                    enablePlay(true)
                }
            }
        }

        toggleButtonPlay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableRecord(false)
                startPlaybackFromTimestamp(playProgress)
                imageView.visibility = View.VISIBLE
            }
            else {
                imageView.visibility = View.INVISIBLE
                resetPlayback()
                enableRecord(true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playProgress = progress.toLong()
                    displayGazeData(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                suspendPlayback()
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                if (isPlaying) {
                    startPlayback()
                }
            }
        })
    }

    private fun enableRecord(bool : Boolean) {
        toggleButtonRecord.isEnabled = bool
        toggleButtonRecord.isClickable = bool
    }

    private fun enablePlay(bool : Boolean) {
        toggleButtonPlay.isEnabled = bool
        toggleButtonPlay.isClickable = bool
    }

    private fun startPlaybackFromTimestamp(timestamp: Long) {
        if (playback.isActive) {
            playback.cancel()
        }

        isPlaying = true
        playProgress = timestamp
        playback = lifecycleScope.launch {
            while(playProgress < lastTimestamp) {
                displayGazeData(playProgress)
                seekBar.setProgress(playProgress.toInt())
                val nextTimestamp = gazeData.keys.first { k -> k > playProgress }
                delay(nextTimestamp - playProgress)
                playProgress = nextTimestamp
            }
            displayGazeData(playProgress)
            seekBar.progress = playProgress.toInt()
            isPlaying = false
        }
    }

    private fun startPlayback() {
        startPlaybackFromTimestamp(playProgress)
    }

    private fun pausePlayback() {
        playback.cancel()
        isPlaying = false
    }

    private fun suspendPlayback() {
        playback.cancel()
    }

    private fun resetPlayback() {
        playback.cancel()
        isPlaying = false
        playProgress = 0
    }

    private fun displayGazeData(timestamp : Long) {
        var coords = gazeData[timestamp]
        if (coords == null) {
            val closestTimestamp = gazeData.keys.findLast { k -> k < timestamp }
            coords = gazeData[closestTimestamp]
        }

        if (coords != null) {
            imageView.translationX = coords.first
            imageView.translationY = coords.second
        }
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

        runOnUiThread(Runnable {
            enableRecord(true)
        })
    }

    private val oneEuroFilterManager = OneEuroFilterManager(2)

    private val gazeCallback = GazeCallback { gazeInfo ->
        if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            val filteredValues = oneEuroFilterManager.filteredValues
            val filteredX = filteredValues[0]
            val filteredY = filteredValues[1]

            gazeData[gazeInfo.timestamp] = Pair(filteredX, filteredY)
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