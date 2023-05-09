package com.DTU.concussionclient

import android.Manifest
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private val concussionApplication get() = application as ConcussionApplication
    private val preferences get() = concussionApplication.getPreferences(this)
    private val hasBaseline get() = !preferences.getFloat("Baseline", Float.NaN).isNaN()

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)
    private val permissionRequestCode = 1000

    private lateinit var postInjuryTestFragment : FrontPageTestButtonFragment
    private lateinit var baselineTestFragment : FrontPageTestButtonFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disable changing orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.clearDataButton).setOnClickListener {
            with(preferences.edit()) {
                clear()
                apply()
            }

            updateDebugData()
        }

        updateDebugData()
        checkPermission()
    }

    private fun checkPermission() {
        if (!hasPermissions(permissions)) {
            requestPermissions(permissions, permissionRequestCode)
        }
        else {
            onCameraAndAudioPermissionGranted(true, true)
        }
    }

    private fun onCameraAndAudioPermissionGranted(cameraIsGranted : Boolean, audioIsGranted : Boolean) {
        if (cameraIsGranted && audioIsGranted) {
            concussionApplication.initGazeRecorder(::enableTestButtons, ::onGazeRecorderInitFail)
        }
        else if (audioIsGranted) {
            enableTestButtons()
        }
    }

    private fun enableTestButtons() {
        runOnUiThread {
            postInjuryTestFragment.enableTestButton(true)
            baselineTestFragment.enableTestButton(true)
        }
    }

    private fun onGazeRecorderInitFail(error : String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Failed to initialize gaze recorder")
        alertDialogBuilder.setMessage(error)
        alertDialogBuilder.setPositiveButton("Retry initialization") { _, _ ->
            concussionApplication.initGazeRecorder(::enableTestButtons, ::onGazeRecorderInitFail)
        }
        alertDialogBuilder.setNegativeButton("Continue without gaze tracking") {_, _ ->
            enableTestButtons()
        }
        runOnUiThread {
            alertDialogBuilder.show()
        }
    }

    private fun hasPermissions(permissions : Array<String>) : Boolean {
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestCode -> if (grantResults.size > 0) {
                val cameraPermissionIndex = permissions.indexOf(Manifest.permission.CAMERA)
                val audioPermissionIndex = permissions.indexOf(Manifest.permission.RECORD_AUDIO)
                if (cameraPermissionIndex != -1 && audioPermissionIndex != -1) {
                    val cameraPermissionGranted = grantResults[cameraPermissionIndex] == PackageManager.PERMISSION_GRANTED
                    val audioPermissionGranted = grantResults[audioPermissionIndex] == PackageManager.PERMISSION_GRANTED
                    onCameraAndAudioPermissionGranted(cameraPermissionGranted, audioPermissionGranted)
                }
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if(fragment.id == R.id.postinjuryFragment)
        {
            fragment.arguments = getTestButtonFragmentBundle(
                "Post-Injury Test",
                "Has an accident or injury left you dizzy, disoriented, or unconscious? " +
                        "Assess your injury and seek a medical professional immediately!",
                R.drawable.post_injury_button,
                isScreening = true,
                isEnabled = hasBaseline)

            postInjuryTestFragment = fragment as FrontPageTestButtonFragment
        }
        else if(fragment.id == R.id.baselineFragment)
        {
            fragment.arguments = getTestButtonFragmentBundle(
                "Baseline Test",
                "It’s important to regularly establish a baseline of your performance in" +
                        "order to accurately assess your injury in case of an accident",
                R.drawable.baseline_button,
                isScreening = false,
                isEnabled = true)
            baselineTestFragment = fragment as FrontPageTestButtonFragment
        }
    }

    private fun getTestButtonFragmentBundle(title: String, body: String, image: Int, isScreening: Boolean, isEnabled: Boolean) : Bundle
    {
        val bundle = Bundle()
        bundle.putString("TitleText", title)
        bundle.putString("BodyText", body)
        bundle.putInt("ImageResource", image)
        bundle.putBoolean("IsScreening", isScreening)
        bundle.putBoolean("IsEnabled", isEnabled)

        return bundle
    }

    private fun updateDebugData() {
        val textView = findViewById<TextView>(R.id.debugData)
        textView.text = "Baseline Score: ${preferences.getFloat("Baseline", Float.NaN)}"
    }
}