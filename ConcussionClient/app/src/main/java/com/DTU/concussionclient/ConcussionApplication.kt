package com.DTU.concussionclient

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.util.prefs.Preferences
import kotlin.math.min

class ConcussionApplication : Application() {
    // Contains information regarding a test from the moment the given test button is pressed until
    // the test has concluded
    data class TestingSession(var instance: TestingInstance, var baselineTempDataCache: BaselineTempDataCache?) {

    }

    // Contains information regarding a single instance of the King-Devick test, from the first
    // demonstration flashcard is shown until the final test flashcard has been completed and reviewed
    data class TestingInstance(var flashcardData: MutableMap<Int, FlashcardData>) {
        fun createFlashcardData(index: Int) {
            flashcardData[index] = FlashcardData(mutableMapOf(), Float.NaN)
        }

        fun getFlashcardData(index: Int) : FlashcardData {
            return flashcardData[index]!!
        }
    }

    data class BaselineTempDataCache(var firstAttempt: Float = Float.NaN, var secondAttempt: Float = Float.NaN) {

        val min get() = min(firstAttempt, secondAttempt)
    }

    data class FlashcardData(var numbers: MutableMap<Int, FlashcardNumberData>, var elapsedTime: Float) {
    }

    data class FlashcardNumberData(val index: Int, val expectedValue: Int, var actualValue: Int) {
    }

    public val getBaselineTempData get() = getSession.baselineTempDataCache!!
    public val getSession get() = session!!
    public val getInstance get() = getSession.instance
    public val getIsScreening get() = isScreening!!

    lateinit var gazeRecorder : SeeSoGazeRecorder
    private var session: TestingSession? = null
    private var isScreening: Boolean? = null

    fun getPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences("concussion", Context.MODE_PRIVATE)
    }

    fun initializeNewSession(isScreening: Boolean) {
        this.isScreening = isScreening
        session = TestingSession(
                    createNewInstance(),
                    if (!isScreening) BaselineTempDataCache() else null)
    }

    // Resets all data for the current instance
    fun clearInstance() {
        getSession.instance = createNewInstance()
    }

    fun calculateFinalScore() : Float {
        var finalValue = 0.0f

        for (keyValuePair in getInstance.flashcardData) {
            // Demonstration flashcard
            if(keyValuePair.key == 0)
                continue

            val flashcardScore = calculateFlashcardScore(keyValuePair.value)
            finalValue += flashcardScore
        }

        return finalValue
    }

    private fun calculateFlashcardScore(data: FlashcardData) : Float {
        return data.elapsedTime
    }

    private fun createNewInstance() : TestingInstance {
        return TestingInstance(mutableMapOf())
    }

    fun initGazeRecorder(onInitSuccess : () -> Unit) {
        gazeRecorder = SeeSoGazeRecorder(applicationContext, onInitSuccess, ::onRecorderInitFail)
    }

    private fun onRecorderInitFail(error : String) {
    }
}