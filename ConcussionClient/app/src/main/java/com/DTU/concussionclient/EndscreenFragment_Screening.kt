package com.DTU.concussionclient

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import com.DTU.concussionclient.databinding.FragmentEndscreenScreeningBinding
import com.DTU.concussionclient.databinding.FragmentFullscreenPopupBinding

class EndscreenFragment_Screening : Fragment() {

    private val concussionApplication get() = (context as Activity).application as ConcussionApplication
    private val preferences get() = concussionApplication.getPreferences(requireContext())
    private val baselineScore get() = preferences.getFloat("Baseline", Float.NaN)
    private val screeningScore get() = concussionApplication.calculateFinalScore()
    private val isConcussionDetected get() = baselineScore < screeningScore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = FragmentEndscreenScreeningBinding.inflate(inflater, container, false).root

        root.findViewById<ImageView>(R.id.screeningTopLeftImage).setImageResource(getIcon())
        root.findViewById<ImageView>(R.id.screeningTopRightImage).setImageResource(getIcon())

        val headerText = root.findViewById<TextView>(R.id.screeningHeaderText)
        headerText.text = getHeaderText()
        headerText.setTextColor(ContextCompat.getColor(requireContext(), getHeaderColor()))

        root.findViewById<TextView>(R.id.disclaimerText).text = getDisclaimerText()

        if(!isConcussionDetected)
            root.findViewById<TextView>(R.id.consultMedicalProfessionalText).text = ""

        root.findViewById<TextView>(R.id.baselineText).text = "Baseline Threshold: ${baselineScore}"
        root.findViewById<TextView>(R.id.screeningScoreText).text = "Screening Score: ${screeningScore}"

        val button = root.findViewById<Button>(R.id.screeningContinueButton)
        button.setOnClickListener {
            val newIntent = Intent(activity, MainActivity::class.java)
            startActivity(newIntent)
        }

        return root
    }

    private fun getDisclaimerText(): String {
        return if (isConcussionDetected) resources.getString(R.string.diagnose_text_concussion)
                else resources.getString(R.string.diagnose_text_healthy)
    }

    private fun getHeaderColor(): Int {
        return if (isConcussionDetected) R.color.concussion_red else R.color.healthy_green
    }

    private fun getHeaderText(): String {
        return if (isConcussionDetected) "Possible Concussion Detected" else "Screening Clear"
    }

    private fun getIcon(): Int {
        return if (isConcussionDetected) R.drawable.post_injury_button else R.drawable.baseline_button
    }
}