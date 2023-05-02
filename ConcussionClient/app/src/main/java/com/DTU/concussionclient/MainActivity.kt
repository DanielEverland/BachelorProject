package com.DTU.concussionclient

import android.Manifest
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private val permissionRequestCode = 1000

    private lateinit var postInjuryTestFragment : FrontPageTestButtonFragment
    private lateinit var baselineTestFragment : FrontPageTestButtonFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disable changing orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        // Hides the title bar
        supportActionBar?.hide()

        checkPermission()
    }

    private fun checkPermission() {
        if (!hasPermissions(permissions)) {
            requestPermissions(permissions, permissionRequestCode)
        }
        else {
            onPermissionGranted(true)
        }
    }

    private fun onPermissionGranted(isGranted : Boolean) {
        if (isGranted) {
                (application as ConcussionApplication).initGazeRecorder(::enableTestButtons)
        }
    }

    private fun enableTestButtons() {
        runOnUiThread {
            postInjuryTestFragment.enableTestButton(true)
            baselineTestFragment.enableTestButton(true)
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
                val cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraPermissionAccepted) {
                    onPermissionGranted(true)
                } else {
                    onPermissionGranted(false)
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
                R.drawable.post_injury_button)
            postInjuryTestFragment = fragment as FrontPageTestButtonFragment
        }
        else if(fragment.id == R.id.baselineFragment)
        {
            fragment.arguments = getTestButtonFragmentBundle(
                "Baseline Test",
                "It’s important to regularly establish a baseline of your performance in" +
                        "order to accurately assess your injury in case of an accident",
                R.drawable.baseline_button)
            baselineTestFragment = fragment as FrontPageTestButtonFragment
        }
        else if(fragment.id == R.id.concussionFooter)
        {
            fragment.arguments = getFooterFragmentBundle(
                "About\nConcussions",
                R.drawable.concussionicon
            )
        }
        else if(fragment.id == R.id.historyFooter)
        {
            fragment.arguments = getFooterFragmentBundle(
                "My Test\nHistory",
                R.drawable.historyicon
            )
        }
    }

    private fun getTestButtonFragmentBundle(title: String, body: String, image: Int) : Bundle
    {
        val bundle = Bundle()
        bundle.putString("TitleText", title)
        bundle.putString("BodyText", body)
        bundle.putInt("ImageResource", image)

        return bundle
    }

    private fun getFooterFragmentBundle(text: String, image: Int) : Bundle
    {
        val bundle = Bundle()
        bundle.putInt("ImageResource", image)
        bundle.putString("Text", text)

        return bundle
    }
}