package com.DTU.concussionclient

import android.app.Application

class ConcussionApplication : Application() {
    lateinit var gazeRecorder : SeeSoGazeRecorder

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

    fun initializeNewSession() {
        session = TestingSession(mutableMapOf())
    }

    private fun onRecorderInitFail(error : String) {
    }
}