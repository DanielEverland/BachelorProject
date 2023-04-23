package com.DTU.concussionclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class EndscreenFragment_Baseline : Fragment() {

    private val concussionApplication get() = ((context as Activity).application as ConcussionApplication)
    private val preferences get() = concussionApplication.getPreferences(requireContext())
    private val baselineData get() = concussionApplication.getBaselineTempData
    private val isFirstAttempt get() = baselineData.secondAttempt.isNaN()
    private val savedBaselineScore get() = preferences.getFloat("Baseline", Float.NaN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionBestScore = getSessionBestScore()
        if(savedBaselineScore.isNaN() || savedBaselineScore > sessionBestScore) {
            with(preferences.edit()) {
                putFloat("Baseline", sessionBestScore)
                apply()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_endscreen__baseline, container, false)

        root.findViewById<Button>(R.id.baselineContinueButton).setOnClickListener {
            if(isFirstAttempt) {
                concussionApplication.clearInstance()

                val newIntent = Intent(activity, TestActivity::class.java)
                newIntent.putExtra("FlashcardIndex", 0)
                newIntent.putExtra("IsScreening", false)
                startActivity(newIntent)
            }
            else {
                val newIntent = Intent(activity, MainActivity::class.java)
                startActivity(newIntent)
            }
        }

        root.findViewById<TextView>(R.id.firstBaselineValue).text = baselineData.firstAttempt.toString()
        root.findViewById<TextView>(R.id.secondBaselineValue).text = baselineData.secondAttempt.toString()

        setDescription(root.findViewById(R.id.baselinePrimaryDescription))
        setFinalScoreText(root.findViewById(R.id.scoreText))
        setButtonText(root.findViewById(R.id.baselineContinueButton))

        return root
    }

    private fun setDescription(textView: TextView) {
        if(isFirstAttempt) {
            textView.text = "You have completed the first round.\n" +
                    "Press continue to start the second round."
        }
        else {
            var text: String = "You have completed the second round\n" +
                    "The test is now complete"

            if(getSessionBestScore() > preferences.getFloat("Baseline", Float.NaN)) {
                text += "\n\nYour previous baseline was better than this session's\n" +
                        "so your baseline will not be altered."
            }

            textView.text = text
        }
    }

    private fun setFinalScoreText(textView: TextView) {
        if(isFirstAttempt) {
            textView.text = ""
        }
        else {
            textView.text = "Final Baseline: ${preferences.getFloat("Baseline", Float.NaN)}"
        }
    }

    private fun setButtonText(button: Button) {
        button.text = if(isFirstAttempt) "Continue" else "Finish"
    }

    private fun getSessionBestScore(): Float {
        return baselineData.min
    }
}