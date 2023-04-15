package com.DTU.concussionclient

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
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
    public var isDiagonal = false

    private val linePaint = Paint()
    private val debugPaint = Paint()
    private val margin = 20.0f
    private val arrowWidth = 20.0f
    private val arrowHeight = 30.0f
    private val arrowHalfHeight = arrowHeight / 2.0f
    private val diagonalHorizontalOffset = 40.0f

    private var midHeight = 0.0f

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        linePaint.strokeWidth = 4.0f
        linePaint.color = Color.WHITE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            debugPaint.color = Color.argb(0.3f, 1.0f, 0.0f, 0.0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isDiagonal) drawDiagonal(canvas) else drawMidline(canvas)
    }

    private fun drawDiagonal(canvas: Canvas) {
        val left = diagonalHorizontalOffset
        val top = 0.0f
        val right = width - diagonalHorizontalOffset
        val bottom = height.toFloat()

        canvas.drawLine(left, bottom, right, top, linePaint)

        if(showArrow)
            drawArrow(canvas, left, top, right, bottom)
    }

    private fun drawMidline(canvas: Canvas) {
        midHeight = height / 2.0f
        val xStart = margin + startXOffset
        val yStart = midHeight + startYOffset
        val xEnd = width.toFloat() - margin + endXOffset
        val yEnd = midHeight + endYOffset
        canvas.drawLine(xStart, yStart, xEnd, yEnd, linePaint)

        if(showArrow)
            drawArrow(canvas, xStart, yStart, xEnd, yEnd)
    }

    fun drawArrow(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val xDiff = right - left
        val yDiff = bottom - top

        val arrowAnchorX = left
        val arrowAnchorY = bottom
        val directionMagnitude = if (reverseArrowDirection) -1.0f else 1.0f

        var arrowDistance = arrowOffset

        // If the arrow direction is reversed, it has to be placed at the end of the
        // hypotenuse relative to the lower left corner
        if(reverseArrowDirection)
        {
            // Use pythagoras theorem to determine hypotenuse length
            val hypLength = sqrt(xDiff.pow(2) + yDiff.pow(2))
            arrowDistance = hypLength - arrowOffset
        }

        // Tan(theta) = opposite / adjacent
        val tanTheta = yDiff / xDiff
        val theta = atan(tanTheta)
        val sinTheta = sin(theta)

        // Since the triangles are similar, we can reuse the angle and plug in the arrow
        // distance as the hypotenuse to get the y-offset, which is equivalent to the
        // leg opposite of the angle
        // sin(theta) = opposite / hypotenuse => opposite = sin(theta) * hypotenuse
        val arrowYOffset = sinTheta * arrowDistance

        // Now we finally need to know how far to offset along the x-axis, which can be done
        // using pythagoras theorem:
        // a^2 + b^2 = c^2 => a^2 = c^2 - b^2 => a = sqrt(c^2 - b^2)
        val arrowXOffset = sqrt(arrowDistance.pow(2) - arrowYOffset.pow(2))

        val arrowStartX = arrowAnchorX + arrowXOffset
        val arrowStartY = arrowAnchorY - arrowYOffset

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