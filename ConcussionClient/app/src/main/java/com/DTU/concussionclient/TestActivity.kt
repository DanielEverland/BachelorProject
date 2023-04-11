package com.DTU.concussionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Disable changing orientation
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.debugNextFlashcardButton).setOnClickListener {
            val newIntent = Intent(this, TestActivity::class.java)
            newIntent.putExtra("FlashcardIndex", intent.extras!!.getInt("FlashcardIndex") + 1)
            startActivity(newIntent)
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", intent.extras!!.getInt("FlashcardIndex"))
        fragment.arguments = bundle
    }
}