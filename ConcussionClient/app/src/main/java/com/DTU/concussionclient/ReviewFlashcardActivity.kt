package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.DTU.concussionclient.databinding.ActivityReviewFlashcard2Binding

class ReviewFlashcardActivity : AppCompatActivity() {

    private val seed get() = intent.extras!!.getInt("Seed")
    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_flashcard)

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.debugNextTestButton).setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("FlashcardIndex", getFlashcardIndex + 1)
            startActivity(intent)
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        val bundle = Bundle()
        bundle.putInt("Index", getFlashcardIndex)
        bundle.putInt("Seed", seed)
        fragment.arguments = bundle
    }
}