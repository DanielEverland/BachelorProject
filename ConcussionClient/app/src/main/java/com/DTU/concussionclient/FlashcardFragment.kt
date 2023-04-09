package com.DTU.concussionclient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import android.widget.Space
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FlashcardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlashcardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_flashcard, container, false)
        val layout = root as LinearLayout
        layout.clipChildren = false

        val rows = 5
        val numbers = 5

        addSpace(layout, 0.25f)
        createNumberRows(rows, layout, numbers)
        addSpace(layout, 0.25f)
        createFooterText(layout)
        addSpace(layout, 0.15f)

        return root
    }

    private fun createNumberRows(
        rows: Int,
        layout: LinearLayout,
        numbers: Int
    ) {
        for (i in 1..rows) {
            val row = LinearLayout(activity)
            row.orientation = LinearLayout.HORIZONTAL
            row.clipChildren = false
            layout.addView(row)

            for (j in 1..numbers) {
                val newView = TextView(activity)
                newView.text = (1..9).random().toString()
                row.addView(newView)

                if (j != numbers) {
                    val line = addLine(row, (1..5).random().toFloat())
                    line.showArrow = j == 1
                }
            }

            if (i != rows)
                addFillingSpace(layout)
        }
    }

    private fun createFooterText(layout: LinearLayout) {
        val textView = TextView(activity)
        textView.text = "Demonstration Card (1/4)"
        textView.textAlignment = TEXT_ALIGNMENT_CENTER
        layout.addView(textView)
        (textView.layoutParams as LayoutParams).height = 50
    }

    private fun addFillingSpace(parentLayout: ViewGroup)
    {
        //addSpace(parentLayout, 1.0f)
        addDiagonalLine(parentLayout, 1.0f)
    }

    private fun addSpace(parentLayout: ViewGroup, Weight: Float)
    {
        val newSpace = Space(activity)
        parentLayout.addView(newSpace)
        (newSpace.layoutParams as LayoutParams).weight = Weight
    }

    private fun addDiagonalLine(parentLayout: ViewGroup, Weight: Float)
    {
        val newLine = Line(activity as Context)
        parentLayout.addView(newLine)
        (newLine.layoutParams as LayoutParams).weight = Weight

        newLine.startXOffset = 20.0f
        newLine.startYOffset = 90.0f
        newLine.endXOffset = -20.0f
        newLine.endYOffset = -90.0f
        newLine.showArrow = true
        newLine.reverseArrowDirection = true
        newLine.arrowOffset = 120.0f
    }

    private fun addLine(parentLayout: ViewGroup, Weight: Float) : Line
    {
        val newLine = Line(activity as Context)
        parentLayout.addView(newLine)
        (newLine.layoutParams as LayoutParams).weight = Weight
        (newLine.layoutParams as LayoutParams).height = 50
        (newLine.parent as ViewGroup).clipChildren = false
        newLine.arrowOffset = 40.0f

        return newLine
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FlashcardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FlashcardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}