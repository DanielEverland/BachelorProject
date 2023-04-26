package com.DTU.concussionclient

import android.app.Application
import android.media.MediaRecorder
import java.io.File

class ConcussionApplication : Application() {
    lateinit var gazeRecorder : SeeSoGazeRecorder
    lateinit var audioRecorder : MediaRecorder
    val audioFilePath : String = File.createTempFile("kingdevick", ".mp3").path

    data class FlashcardNumberData(val index: Int, val expectedValue: Int, var actualValue: Int) {
    }

    data class FlashcardData(var numbers: MutableMap<Int, FlashcardNumberData>, var elapsedTime: Float) {
    }

    data class TestingSession(var flashcardData: MutableMap<Int, FlashcardData>) {
        fun createFlashcardData(index: Int) {
            flashcardData[index] = FlashcardData(mutableMapOf(), Float.NaN)
        }

        fun getFlashcardData(index: Int) : FlashcardData {
            return flashcardData[index]!!
        }
    }

    public val getSession get() = session!!

    private var session: TestingSession? = null


    fun initGazeRecorder(onInitSuccess : () -> Unit) {
        gazeRecorder = SeeSoGazeRecorder(applicationContext, onInitSuccess, ::onRecorderInitFail)
    }

    fun initAudioRecorder() {
        audioRecorder = MediaRecorder()
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        audioRecorder.setOutputFile(audioFilePath)
        audioRecorder.prepare()
    }

    fun initializeNewSession() {
        session = TestingSession(mutableMapOf())
    }

    private fun onRecorderInitFail(error : String) {
    }
}