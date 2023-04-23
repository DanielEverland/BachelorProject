package com.DTU.concussionclient

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val concussionApplication get() = application as ConcussionApplication
    private val preferences get() = concussionApplication.getPreferences(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disable changing orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

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
                true)
        }
        else if(fragment.id == R.id.baselineFragment)
        {
            fragment.arguments = getTestButtonFragmentBundle(
                "Baseline Test",
                "It’s important to regularly establish a baseline of your performance in" +
                        "order to accurately assess your injury in case of an accident",
                R.drawable.baseline_button,
                false)
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

    private fun getTestButtonFragmentBundle(title: String, body: String, image: Int, isScreening: Boolean) : Bundle
    {
        val bundle = Bundle()
        bundle.putString("TitleText", title)
        bundle.putString("BodyText", body)
        bundle.putInt("ImageResource", image)
        bundle.putBoolean("IsScreening", isScreening)

        return bundle
    }

    private fun getFooterFragmentBundle(text: String, image: Int) : Bundle
    {
        val bundle = Bundle()
        bundle.putInt("ImageResource", image)
        bundle.putString("Text", text)

        return bundle
    }

    private fun updateDebugData() {
        val textView = findViewById<TextView>(R.id.debugData)
        textView.text = "Baseline Score: ${preferences.getFloat("Baseline", Float.NaN)}"
    }
}