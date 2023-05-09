package com.DTU.concussionclient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class ReviewFlashcardActivity : AppCompatActivity() {

    private val seed get() = intent.extras!!.getInt("Seed")
    private val getFlashcardIndex get() = intent.extras!!.getInt("FlashcardIndex")
    private val getTimeElapsed get() = getFlashcardData.elapsedTime
    private val concussionApplication get() = (application as ConcussionApplication)
    private val getFlashcardData get() = concussionApplication.getInstance.flashcardData[getFlashcardIndex]!!
    private val isFinalFlashcard get() = getFlashcardIndex >= 3

    private var selectedIndex = 0

    private lateinit var playbackControlContainer : LinearLayout
    private lateinit var playPauseButton : ImageButton
    private lateinit var playbackBar : SeekBar
    private lateinit var currentTimeText : TextView
    private lateinit var maxTimeText : TextView
    private lateinit var gazeIndicatorView : ImageView
    private lateinit var flashcardContainer : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_flashcard)

        playbackControlContainer = findViewById(R.id.playbackControlContainer)
        playPauseButton = findViewById(R.id.playPauseButton)
        playbackBar = findViewById(R.id.playbackBar)
        currentTimeText = findViewById(R.id.currentTimeText)
        maxTimeText = findViewById(R.id.maxTimeText)
        gazeIndicatorView = findViewById(R.id.gazeIndicatorView)
        flashcardContainer = findViewById(R.id.flashcardContainer)

        // Hides the title bar
        supportActionBar?.hide()

        val viewModel : ReviewFlashcardViewModel by viewModels()
        viewModel.initGazePlayer()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.maxProgress != null && it.maxProgress > 0) {
                        playbackControlContainer.visibility = View.VISIBLE
                        if (it.isPlaying) {
                            playPauseButton.setImageResource(R.drawable.pause_icon)
                        }
                        else {
                            playPauseButton.setImageResource(R.drawable.play_icon)
                        }

                        currentTimeText.text = viewModel.getTimestampText(it.playProgress)
                        maxTimeText.text = viewModel.getTimestampText(it.maxProgress)

                        playbackBar.max = it.maxProgress
                        playbackBar.progress = it.playProgress

                        val width = flashcardContainer.width
                        val height = flashcardContainer.height
                        if (it.indicatorX == null || it.indicatorY == null || width == 0 || height == 0) {
                            gazeIndicatorView.visibility = View.GONE
                        }
                        else {
                            gazeIndicatorView.translationX = it.indicatorX * width
                            gazeIndicatorView.translationY = it.indicatorY * height
                            gazeIndicatorView.visibility = View.VISIBLE
                        }
                    }
                    else {
                        playbackControlContainer.visibility = View.INVISIBLE
                    }
                }
            }
        }

        playPauseButton.setOnClickListener {
            viewModel.togglePlay()
        }

        playbackBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                viewModel.blockPlayback()
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                viewModel.unblockPlayback()
            }
        })

        findViewById<Button>(R.id.debugNextTestButton).setOnClickListener {
            viewModel.stopPlayback()
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

        getFlashcardData.elapsedTime = intent.extras!!.getFloat("TimeElapsed")
        val elapsedtimeEditText = findViewById<EditText>(R.id.elapsedTimeEditText)
        elapsedtimeEditText.setText(getTimeElapsed.toString())
        elapsedtimeEditText.doAfterTextChanged {
            if(!elapsedtimeEditText.text.isEmpty())
                getFlashcardData.elapsedTime = elapsedtimeEditText.text.toString().toFloat()
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
        }
    }
}