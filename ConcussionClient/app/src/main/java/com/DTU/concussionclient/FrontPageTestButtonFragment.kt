package com.DTU.concussionclient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TitleArg = "TitleText"
private const val BodyArg = "BodyText"
private const val ImageArg = "ImageResource"

/**
 * A simple [Fragment] subclass.
 * Use the [FrontPageTestButtonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FrontPageTestButtonFragment : Fragment() {
    private var titleText: String? = null
    private var bodyText: String? = null
    private var imageResource: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            titleText = it.getString(TitleArg)
            bodyText = it.getString(BodyArg)
            imageResource = it.getInt(ImageArg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_front_page_test_button, container, false)

        root.findViewById<TextView>(R.id.testButtonFragmentTitle).text = titleText
        root.findViewById<TextView>(R.id.testButtonFragmentText).text = bodyText

        imageResource?.let {
            root.findViewById<ImageButton>(R.id.testButtonFragmentButton).setImageResource(it)
        }

        root.findViewById<ImageButton>(R.id.testButtonFragmentButton).setOnClickListener {

        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FrontPageTestButtonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: Int) =
            FrontPageTestButtonFragment().apply {
                arguments = Bundle().apply {
                    putString(TitleArg, param1)
                    putString(BodyArg, param2)
                    putInt(ImageArg, param3)
                }
            }
    }
}