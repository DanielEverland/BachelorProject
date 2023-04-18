package com.DTU.concussionclient

import android.app.Application

class ConcussionApplication : Application() {
    data class FlashcardNumberData(val index: Int, val expectedValue: Int, var actualValue: Int) {
    }

    data class FlashcardData(var numbers: MutableMap<Int, FlashcardNumberData>) {
    }

    data class TestingSession(var flashcardData: MutableMap<Int, FlashcardData>) {
        fun createFlashcardData(index: Int) {
            flashcardData[index] = FlashcardData(mutableMapOf())
        }

        fun getFlashcardData(index: Int) : FlashcardData {
            return flashcardData[index]!!
        }
    }

    public val getSession get() = session!!

    private var session: TestingSession? = null

    fun initializeNewSession() {
        session = TestingSession(mutableMapOf())
    }
}