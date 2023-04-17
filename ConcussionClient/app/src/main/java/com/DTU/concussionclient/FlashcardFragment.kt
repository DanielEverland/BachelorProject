package com.DTU.concussionclient

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import android.widget.Space
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "Index"
private const val ARG_PARAM2 = "Seed"

class FlashcardConfiguration(lineConfiguration: LineConfiguration, val name: String, val rows: Int,
    val headerWeight: Float, val footerWeight: Float) {
    class LineConfiguration(val showLines: Boolean, val showArrows: Boolean, val showDiagonal: Boolean) {
    }

    val line = lineConfiguration
}

/**
 * A simple [Fragment] subclass.
 * Use the [FlashcardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlashcardFragment : Fragment() {
    data class FlashcardNumberData(val index: Int, val value: Int) {
    }

    interface OnClickListener {
        fun onClick(data: FlashcardNumberData)
    }

    private var configuration: FlashcardConfiguration? = null
    private var seed: Int? = null
    private var randomGenerator: Random? = null
    private var clickListener: OnClickListener? = null
    private var numberData: MutableMap<Int, FlashcardNumberData> = mutableMapOf()
    private var numberUILookup: MutableMap<View, Int> = mutableMapOf()

    private val allConfigs = arrayOf(
        FlashcardConfiguration(
            FlashcardConfiguration.LineConfiguration(showLines = true, showArrows = true, showDiagonal = true),
            "Demonstration Card (1/4)",
            5, 0.25f, 0.25f),

        FlashcardConfiguration(
            FlashcardConfiguration.LineConfiguration(showLines = true, showArrows = false, showDiagonal = false),
            "Test I (2/4)",
            8, 0.25f, 0.25f),

        FlashcardConfiguration(
            FlashcardConfiguration.LineConfiguration(showLines = false, showArrows = false, showDiagonal = false),
            "Test II (3/4)",
            8, 0.25f, 0.25f),

        FlashcardConfiguration(
            FlashcardConfiguration.LineConfiguration(showLines = false, showArrows = false, showDiagonal = false),
            "Test III (4/4)",
            8, 6.0f, 15.0f)
    )

    fun getNumberData(index: Int) : FlashcardNumberData {
        return numberData[index]!!
    }

    fun setOnClickListener(listener: OnClickListener) {
        clickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            configuration = allConfigs[it.getInt(ARG_PARAM1)]
            seed = it.getInt(ARG_PARAM2)
            randomGenerator = Random(seed!!)
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

        val rows = configuration!!.rows
        val numbers = 5

        addSpace(layout, configuration!!.headerWeight)
        createNumberRows(rows, layout, numbers)
        addSpace(layout, configuration!!.footerWeight)
        createFooterText(layout)
        addSpace(layout, 0.15f)

        return root
    }

    private fun createNumberRows(
        rows: Int,
        layout: LinearLayout,
        numbers: Int
    ) {
        var numberIndex = 0
        for (i in 1..rows) {
            val row = LinearLayout(activity)
            row.orientation = LinearLayout.HORIZONTAL
            row.clipChildren = false
            layout.addView(row)

            for (j in 1..numbers) {
                row.addView(createNewTextView(numberIndex++))

                if (j != numbers) {
                    val weight = randomGenerator!!.nextInt(1, 6).toFloat()
                    if(configuration!!.line.showLines)
                    {
                        val line = addLine(row, weight)
                        line.showArrow = j == 1 && configuration!!.line.showArrows
                    }
                    else
                    {
                        addSpace(row, weight)
                    }
                }
            }

            if (i != rows)
                addFillingSpace(layout)
        }
    }

    private fun createNewTextView(numberIndex: Int): TextView {
        val newNumber = randomGenerator!!.nextInt(1, 10)

        val newView = TextView(activity)
        newView.text = newNumber.toString()
        newView.setOnClickListener {
            val data = numberData[numberUILookup[it]!!]!!
            Log.i("FlashcardFragment", "Pressed Flashcard Number. Index: ${data.index}, Number: ${data.value}")
            clickListener?.onClick(data)
        }
        numberData[numberIndex] = FlashcardNumberData(numberIndex, newNumber)
        numberUILookup[newView] = numberIndex

        return newView
    }

    private fun createFooterText(layout: LinearLayout) {
        val textView = TextView(activity)
        textView.text = configuration!!.name
        textView.textAlignment = TEXT_ALIGNMENT_CENTER
        layout.addView(textView)
        (textView.layoutParams as LayoutParams).height = 50
    }

    private fun addFillingSpace(parentLayout: ViewGroup)
    {
        if (configuration!!.line.showDiagonal) addDiagonalLine(parentLayout, 1.0f)
        else addSpace(parentLayout, 1.0f)
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

        newLine.isDiagonal = true
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
         * @param param2 Seed for generating random values
         * @return A new instance of fragment FlashcardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
            FlashcardFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}