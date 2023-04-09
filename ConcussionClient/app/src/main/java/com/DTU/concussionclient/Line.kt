package com.DTU.concussionclient

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.*

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

    public var showArrow = false
    public var reverseArrowDirection = false
    public var startXOffset = 0.0f
    public var startYOffset = 0.0f
    public var endXOffset = 0.0f
    public var endYOffset = 0.0f
    public var arrowOffset = 0.0f

    private val linePaint = Paint()
    private val margin = 20.0f
    private val arrowWidth = 20.0f
    private val arrowHeight = 30.0f
    private val arrowHalfHeight = arrowHeight / 2.0f

    private var midHeight = 0.0f

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        linePaint.strokeWidth = 4.0f
        linePaint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        midHeight = height / 2.0f
        val xStart = margin + startXOffset
        val yStart = midHeight + startYOffset
        val xEnd = width.toFloat() - margin + endXOffset
        val yEnd = midHeight + endYOffset
        canvas.drawLine(xStart, yStart, xEnd, yEnd, linePaint)

        if(showArrow)
            drawArrow(canvas, xStart, yStart, xEnd, yEnd)
    }

    val colors = intArrayOf(
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        -0x1000000, -0x1000000, -0x1000000
    )

    fun drawArrow(canvas: Canvas, xStart: Float, yStart: Float, xEnd: Float, yEnd: Float) {
        val xDiff = xEnd - xStart
        val yDiff = yEnd - yStart

        val arrowAnchorX = if (reverseArrowDirection) xEnd else xStart
        val arrowAnchorY = if (reverseArrowDirection) yEnd else yStart
        val directionMagnitude = if (reverseArrowDirection) -1.0f else 1.0f

        // Tan(theta) = opposite / adjacent
        val tanTheta = yDiff / xDiff
        val theta = atan(tanTheta)
        val sineTheta = sin(theta)

        // Since the triangles are similar, we can reuse the angle and plug in the arrow
        // distance as the hypotenuse to get the y-offset, which is equivalent to the
        // leg opposite of the angle
        // sin(theta) = opposite / hypotenuse => opposite = sin(theta) * hypotenuse
        val arrowYOffset = sineTheta * arrowOffset

        // Now we finally need to know how far to offset along the x-axis, which can be done
        // using pythagoras theorem:
        // a^2 + b^2 = c^2 => a^2 = c^2 - b^2 => a = sqrt(c^2 - b^2)
        val arrowXOffset = sqrt(arrowOffset.pow(2) - arrowYOffset.pow(2))

        val arrowStartX = arrowAnchorX + arrowXOffset * directionMagnitude
        val arrowStartY = arrowAnchorY + arrowYOffset * directionMagnitude

        canvas.drawLine(arrowStartX, arrowStartY, arrowStartX - arrowWidth * directionMagnitude, arrowStartY - arrowHalfHeight, linePaint)
        canvas.drawLine(arrowStartX, arrowStartY, arrowStartX - arrowWidth * directionMagnitude, arrowStartY + arrowHalfHeight, linePaint)
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