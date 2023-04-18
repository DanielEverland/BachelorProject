package com.DTU.concussionclient

import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable

class ReviewFlashcardFragment : FlashcardFragment() {

    override fun updateNumberView(index: Int) {
        val data = numberData[index]!!
        val textView = numberIndexLookup[index]!! as TextView

        if (data.actualValue != data.expectedValue) {
            textView.background = Color.RED.toDrawable()
            textView.setTextColor(Color.BLACK)
        }
        else {
            textView.background = null
            textView.setTextColor(Color.WHITE)
        }
    }
}