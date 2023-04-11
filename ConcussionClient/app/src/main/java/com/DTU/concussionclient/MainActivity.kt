package com.DTU.concussionclient

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disable changing orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        // Hides the title bar
        supportActionBar?.hide()
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
        }
        else if(fragment.id == R.id.baselineFragment)
        {
            fragment.arguments = getTestButtonFragmentBundle(
                "Baseline Test",
                "It’s important to regularly establish a baseline of your performance in" +
                        "order to accurately assess your injury in case of an accident",
                R.drawable.baseline_button)
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