package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val isDemonstrationCard get() = getFlashcardIndex == 0
    private val seed: Int = Random.nextInt();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.debugNextFlashcardButton).setOnClickListener {
            val newIntent = Intent(this, ReviewFlashcardActivity::class.java)
            newIntent.putExtra("FlashcardIndex", getFlashcardIndex)
            newIntent.putExtra("Seed", seed)
            startActivity(newIntent)
        }

        if (isDemonstrationCard)
            createFullscreenPopup()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", getFlashcardIndex)
        bundle.putInt("Seed", seed)
        fragment.arguments = bundle
    }

    private fun createFullscreenPopup() {
        val layout = findViewById<ConstraintLayout>(R.id.fullscreenPopupLayout)

        val popup = FullscreenPopupFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fullscreenPopupLayout, popup)
        ft.commitAllowingStateLoss()
    }
}