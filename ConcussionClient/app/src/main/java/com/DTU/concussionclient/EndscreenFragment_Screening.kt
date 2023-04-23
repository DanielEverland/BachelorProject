package com.DTU.concussionclient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.DTU.concussionclient.databinding.FragmentEndscreenScreeningBinding
import com.DTU.concussionclient.databinding.FragmentFullscreenPopupBinding

class EndscreenFragment_Screening : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentEndscreenScreeningBinding.inflate(inflater, container, false).root
    }
}