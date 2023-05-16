package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private val getInstance get() = concussionApplication.getInstance
    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val isDemonstrationCard get() = getFlashcardIndex == 0
    private val isFirstTestCard get() = getFlashcardIndex == 1
    private val seed: Int = Random.nextInt()
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

            var intent: Intent? = null

            if(isDemonstrationCard)
            {
                intent = Intent(this, TestActivity::class.java)
                intent.putExtra("FlashcardIndex", getFlashcardIndex + 1)
            }
            else
            {
                val timeElapsed = stopRecording()
                intent = Intent(this, ReviewFlashcardActivity::class.java)
                intent.putExtra("FlashcardIndex", getFlashcardIndex)
                intent.putExtra("Seed", seed)
                intent.putExtra("TimeElapsed", timeElapsed)
            }

            startActivity(intent)
        }

        if (isDemonstrationCard) {
            // Launch popup.
            createFullscreenPopup(R.string.demonstration_popup_text) {
                // Do nothing.
            }
        }
        else if (isFirstTestCard) {
            // Launch popup and start recording when dismissed.
            createFullscreenPopup(R.string.test_popup_text) {
                startRecording()
            }
        }
        else {
            // Start recording as soon as fragment is rendered.
            fragmentContainerView.post {
                startRecording()
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", getFlashcardIndex)
        bundle.putInt("Seed", seed)
        fragment.arguments = bundle
    }

    private fun createFullscreenPopup(bodyTextId : Int, onClickCallback : () -> Unit) {
        val popup = FullscreenPopupFragment(bodyTextId, onClickCallback)
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fullscreenPopupLayout, popup)
        ft.commitAllowingStateLoss()

        findViewById<Button>(R.id.debugNextFlashcardButton).isEnabled = false
    }

    private fun startRecording() {
        concussionApplication.initAudioRecorder()
        val offset = IntArray(2)
        fragmentContainerView.getLocationOnScreen(offset)
        startTime = System.currentTimeMillis()
        concussionApplication.audioRecorder.start()
        concussionApplication.gazeRecorder?.startTracking(
            fragmentContainerView.width,
            fragmentContainerView.height,
            offset[0],
            offset[1])
    }

    private fun stopRecording() : Float {
        val timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0f
        concussionApplication.gazeRecorder?.stopTracking()
        concussionApplication.audioRecorder.stop()
        return timeElapsed
    }
}