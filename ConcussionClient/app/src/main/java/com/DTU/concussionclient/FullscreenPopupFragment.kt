package com.DTU.concussionclient

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.DTU.concussionclient.databinding.FragmentFullscreenPopupBinding

class FullscreenPopupFragment : Fragment() {
    private var rootLayout: FragmentFullscreenPopupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootLayout = FragmentFullscreenPopupBinding.inflate(inflater, container, false)

        // Set up the user interaction to manually show or hide the system UI.
        rootLayout!!.fullscreenContent.setOnClickListener { onClicked() }

        return rootLayout!!.root
    }

    private fun onClicked() {
        (context as Activity).findViewById<View>(R.id.debugNextFlashcardButton).isEnabled = true

        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit();
    }
}