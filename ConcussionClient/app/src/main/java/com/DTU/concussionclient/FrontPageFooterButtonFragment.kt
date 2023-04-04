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
private const val ARG_IMAGE_RESOURCE = "ImageResource"
private const val ARG_TEXT = "Text"

/**
 * A simple [Fragment] subclass.
 * Use the [FrontPageFooterButtonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FrontPageFooterButtonFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var imageResource: Int? = null
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageResource = it.getInt(ARG_IMAGE_RESOURCE)
            text = it.getString(ARG_TEXT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_front_page_footer_button, container, false)

        root.findViewById<TextView>(R.id.frontPageFooterText).text = text

        imageResource?.let {
            root.findViewById<ImageButton>(R.id.frontPageFooterButton).setImageResource(it)
        }

        root.findViewById<ImageButton>(R.id.frontPageFooterButton).setOnClickListener {
            Log.i("LogTemp", "ouch")
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imageResource Parameter 1.
         * @param text Parameter 2.
         * @return A new instance of fragment FrontPageFooterButtonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(imageResource: Int, text: String) =
            FrontPageFooterButtonFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RESOURCE, imageResource)
                    putString(ARG_TEXT, text)
                }
            }
    }
}