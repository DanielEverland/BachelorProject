package com.DTU.concussionclient

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.*


class SeeSoActivity : AppCompatActivity() {
    private lateinit var toggleButtonRecord : ToggleButton
    private lateinit var toggleButtonPlay : ToggleButton
    private lateinit var seekBar : SeekBar
    private lateinit var container : ViewGroup

    private var containerWidth : Float = 0F
    private var containerHeight : Float = 0F
    private var indicatorOffsetX : Float = 0F
    private var indicatorOffsetY : Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_so)

        toggleButtonRecord = findViewById(R.id.toggleButtonRecord)
        toggleButtonPlay = findViewById(R.id.toggleButtonPlay)
        seekBar = findViewById(R.id.seekBar)
        container = findViewById(R.id.container)

        val gazeIndicator : ImageView = View.inflate(this, R.layout.gaze_indicator, null) as ImageView
        container.addView(gazeIndicator)
        container.post(Runnable {
            containerWidth = container.width.toFloat()
            containerHeight = container.height.toFloat()
        })
        gazeIndicator.post(Runnable {
            indicatorOffsetX = -gazeIndicator.width / 2F
            indicatorOffsetY = -gazeIndicator.height / 2F
            gazeIndicator.translationX = indicatorOffsetX
            gazeIndicator.translationY = indicatorOffsetY
        })

        val viewModel : SeeSoViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    enableRecord(it.enableRecord)
                    enablePlay(it.enablePlay)

                    if (it.maxProgress != null) {
                        seekBar.visibility = View.VISIBLE
                        seekBar.max = it.maxProgress
                        seekBar.progress = it.playProgress
                    }
                    else {
                        seekBar.visibility = View.INVISIBLE
                        seekBar.max = 0
                        seekBar.progress = 0
                    }

                    if (it.indicatorX != null && it.indicatorY != null) {
                        gazeIndicator.translationX = (it.indicatorX * containerWidth) + indicatorOffsetX
                        gazeIndicator.translationY = (it.indicatorY * containerHeight) + indicatorOffsetY
                        gazeIndicator.visibility = View.VISIBLE
                    }
                    else {
                        gazeIndicator.visibility = View.INVISIBLE
                        gazeIndicator.translationX = indicatorOffsetX
                        gazeIndicator.translationY = indicatorOffsetY
                    }
                }
            }
        }

        toggleButtonRecord.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.startRecording()
            }
            else {
                viewModel.stopRecording()
            }
        }

        toggleButtonPlay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.startPlayback()
            }
            else {
                viewModel.pausePlayback()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setPlaybackProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                viewModel.blockPlayback()
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                viewModel.unblockPlayback()
            }
        })
    }

    private fun enableRecord(bool : Boolean) {
        toggleButtonRecord.isEnabled = bool
        toggleButtonRecord.isClickable = bool
    }

    private fun enablePlay(bool : Boolean) {
        toggleButtonPlay.isEnabled = bool
        toggleButtonPlay.isClickable = bool
    }
}