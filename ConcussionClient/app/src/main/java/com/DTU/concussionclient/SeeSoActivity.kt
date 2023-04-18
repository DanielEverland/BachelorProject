package com.DTU.concussionclient

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class SeeSoActivity : AppCompatActivity() {
    private lateinit var gazeRecorder : SeeSoGazeRecorder
    private lateinit var gazePlayer : SeeSoGazePlayer

    private lateinit var toggleButtonRecord : ToggleButton
    private lateinit var toggleButtonPlay : ToggleButton
    private lateinit var seekBar : SeekBar
    private lateinit var container : ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_so)

        toggleButtonRecord = findViewById(R.id.toggleButtonRecord)
        toggleButtonPlay = findViewById(R.id.toggleButtonPlay)
        seekBar = findViewById(R.id.seekBar)
        container = findViewById(R.id.container)

        enableRecord(false)
        enablePlay(false)

        gazeRecorder = SeeSoGazeRecorder(this, ::initSuccess, ::initFail)

        toggleButtonRecord.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enablePlay(false)
                gazeRecorder.startTracking()
            }
            else {
                gazeRecorder.stopTracking()

                gazePlayer = SeeSoGazePlayer(
                    this,
                    container,
                    gazeRecorder.getGazeData(),
                    ::playbackCallback,
                    ::endPlaybackCallback)

                seekBar.max = gazePlayer.getLastTimestamp()
                enablePlay(true)
            }
        }

        toggleButtonPlay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableRecord(false)
                gazePlayer.startPlayback()
            }
            else {
                gazePlayer.pausePlayback()
                enableRecord(true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    gazePlayer.setPlaybackProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                gazePlayer.blockPlayback()
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                gazePlayer.unblockPlayback()
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

    private fun initSuccess() {
        runOnUiThread(Runnable {
            enableRecord(true)
        })
    }

    private fun initFail(error : String) {
        Log.w("SeeSo", "error description: $error")
    }

    private fun playbackCallback(progress : Int) {
        seekBar.progress = progress
    }

    private fun endPlaybackCallback() {
        toggleButtonPlay.toggle()
    }
}