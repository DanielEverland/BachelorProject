package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment

class ReviewFlashcardActivity : AppCompatActivity(), FlashcardFragment.OnClickListener {

    private val seed get() = intent.extras!!.getInt("Seed")
    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val getTimeElapsed get() = getFlashcardData.elapsedTime
    private val concussionApplication get() = (application as ConcussionApplication)
    private val getFlashcardData get() = concussionApplication.getInstance.flashcardData[getFlashcardIndex]!!
    private val getFlashcardNumber get() = getFlashcardData.numbers[selectedIndex]!!
    private val isFinalFlashcard get() = getFlashcardIndex >= 3

    private var actualNumberView: EditText? = null
    private var flashcard: FlashcardFragment? = null
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_flashcard)

        // Hides the title bar
        supportActionBar?.hide()

        findViewById<Button>(R.id.debugNextTestButton).setOnClickListener {
            if(isFinalFlashcard) {
                val intent = Intent(this, TestEndscreenActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, TestActivity::class.java)
                intent.putExtra("FlashcardIndex", getFlashcardIndex + 1)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.nextErrorButton).setOnClickListener {
            selectNext()
        }

        getFlashcardData.elapsedTime = intent.extras!!.getFloat("TimeElapsed")
        val elapsedtimeEditText = findViewById<EditText>(R.id.elapsedTimeEditText)
        elapsedtimeEditText.setText(getTimeElapsed.toString())
        elapsedtimeEditText.doAfterTextChanged {
            if(!elapsedtimeEditText.text.isEmpty())
                getFlashcardData.elapsedTime = elapsedtimeEditText.text.toString().toFloat()
        }

        actualNumberView = findViewById(R.id.actualNumber)
        actualNumberView!!.doAfterTextChanged {
            val newText = actualNumberView!!.text.toString()
            if(newText.isEmpty())
                return@doAfterTextChanged

            if(newText.length > 1) {
                actualNumberView!!.setText(newText[newText.length - 1].toString())
                actualNumberView!!.setSelection(1)
            }

            getFlashcardNumber.actualValue = actualNumberView!!.text.toString().toInt()

            flashcard!!.flashcardNumberDataUpdated(selectedIndex)
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if(fragment.id == R.id.flashcardFragment)
        {
            val bundle = Bundle()
            bundle.putInt("Index", getFlashcardIndex)
            bundle.putInt("Seed", seed)
            fragment.arguments = bundle

            flashcard = fragment as FlashcardFragment
            flashcard!!.setOnClickListener(this)
        }
    }

    private fun setSelectedNumber(index: Int) {
        val data = flashcard!!.getNumberData(index)

        findViewById<TextView>(R.id.expectedNumber).text = data.expectedValue.toString()
        findViewById<TextView>(R.id.actualNumber).text = data.actualValue.toString()
    }

    private fun selectNext() {
        setSelectedNumber(++selectedIndex)
    }

    override fun onClick(data: ConcussionApplication.FlashcardNumberData) {
        selectedIndex = data.index
        setSelectedNumber(data.index)
    }
}