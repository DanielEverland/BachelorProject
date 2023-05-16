package com.DTU.concussionclient

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.DTU.concussionclient.databinding.FragmentFullscreenPopupBinding

class FullscreenPopupFragment(
    private val bodyTextId : Int,
    private val onClickCallback : () -> Unit
) : Fragment() {
    private var rootLayout: FragmentFullscreenPopupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootLayout = FragmentFullscreenPopupBinding.inflate(inflater, container, false)

        rootLayout!!.root.findViewById<TextView>(R.id.fullscreen_content).text = resources.getString(bodyTextId)

        // Set up the user interaction to manually show or hide the system UI.
        rootLayout!!.fullscreenContent.setOnClickListener { onClicked() }

        return rootLayout!!.root
    }

    private fun onClicked() {
        (context as Activity).findViewById<View>(R.id.debugNextFlashcardButton).isEnabled = true

        onClickCallback()

        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit();
    }
}