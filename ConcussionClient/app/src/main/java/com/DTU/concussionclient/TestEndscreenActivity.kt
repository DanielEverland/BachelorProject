package com.DTU.concussionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class TestEndscreenActivity : AppCompatActivity() {

    private val concussionApplication get() = application as ConcussionApplication
    private val session get() = concussionApplication.getSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_endscreen)

        findViewById<TextView>(R.id.finalScoreText).text = calculateFinalScore().toString()
    }

    private fun calculateFlashcardScore(data: ConcussionApplication.FlashcardData) : Float {
        return data.elapsedTime
    }

    private fun calculateFinalScore() : Float {
        var finalValue = 0.0f

        for (keyValuePair in session.flashcardData) {
            // Demonstration flashcard
            if(keyValuePair.key == 0)
                continue
            
            val flashcardScore = calculateFlashcardScore(keyValuePair.value)
            finalValue += flashcardScore
        }

        return finalValue
    }
}