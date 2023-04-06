package com.DTU.concussionclient

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * TODO: document your custom view class.
 */
class Line : View {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private val linePaint = Paint()

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        linePaint.strokeWidth = 4.0f
        linePaint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val margin = 20.0f
        val midHeight = height / 2.0f
        canvas.drawLine(margin, midHeight, width.toFloat() - margin, midHeight, linePaint)
    }

    // Modified from https://www.geeksforgeeks.org/what-is-onmeasure-custom-view-in-android/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpec = MeasureSpec.getSize(heightMeasureSpec)

        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) widthSpec
                    else min(Int.MIN_VALUE, widthSpec)

        val height =    if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) heightSpec
                        else min(Int.MIN_VALUE, heightSpec)

        setMeasuredDimension(width, height)
    }
}