package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private val getInstance get() = concussionApplication.getInstance
    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val isDemonstrationCard get() = getFlashcardIndex == 0
    private val seed: Int = Random.nextInt();
    private val concussionApplication get() = (application!! as ConcussionApplication)
    private var startTime: Long = 0

    private lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        fragmentContainerView = findViewById(R.id.fragmentContainerView)

        // Hides the title bar
        supportActionBar?.hide()

        getInstance.createFlashcardData(getFlashcardIndex)

        findViewById<Button>(R.id.debugNextFlashcardButton).setOnClickListener {
            concussionApplication.gazeRecorder.stopTracking()

            var intent: Intent? = null

            if(isDemonstrationCard)
            {
                intent = Intent(this, TestActivity::class.java)
                intent.putExtra("FlashcardIndex", getFlashcardIndex + 1)
            }
            else
            {
                intent = Intent(this, ReviewFlashcardActivity::class.java)
                intent.putExtra("FlashcardIndex", getFlashcardIndex)
                intent.putExtra("Seed", seed)
                intent.putExtra("TimeElapsed", (System.currentTimeMillis() - startTime) / 1000.0f)
            }

            startActivity(intent)
        }

        if (isDemonstrationCard)
            createFullscreenPopup()

        fragmentContainerView.post {
            val offset = IntArray(2)
            fragmentContainerView.getLocationOnScreen(offset)
            startTime = System.currentTimeMillis()
            concussionApplication.gazeRecorder.startTracking(
                fragmentContainerView.width,
                fragmentContainerView.height,
                offset[0],
                offset[1])
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", getFlashcardIndex)
        bundle.putInt("Seed", seed)
        fragment.arguments = bundle
    }

    private fun createFullscreenPopup() {
        val popup = FullscreenPopupFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fullscreenPopupLayout, popup)
        ft.commitAllowingStateLoss()

        val continueButton = findViewById<Button>(R.id.debugNextFlashcardButton)
        continueButton.text = "Continue"
        continueButton.isEnabled = false
    }
}