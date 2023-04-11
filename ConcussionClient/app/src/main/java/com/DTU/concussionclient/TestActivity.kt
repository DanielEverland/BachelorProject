package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class TestActivity : AppCompatActivity() {

    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val isDemonstrationCard get() = getFlashcardIndex == 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.debugNextFlashcardButton).setOnClickListener {
            val newIntent = Intent(this, TestActivity::class.java)
            newIntent.putExtra("FlashcardIndex", getFlashcardIndex + 1)
            startActivity(newIntent)
        }

        if (isDemonstrationCard)
            createFullscreenPopup()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", getFlashcardIndex)
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