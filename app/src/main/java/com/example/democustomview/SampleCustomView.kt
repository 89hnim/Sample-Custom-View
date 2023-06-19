package com.example.democustomview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.democustomview.DeviceDimensionsHelper.getDisplayHeight
import com.example.democustomview.DeviceDimensionsHelper.getDisplayWidth

class SampleCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val eventCircleRadius = context.resources.getDimension(R.dimen.circle_radius)
    private val eventCircleBorder = context.resources.getDimension(R.dimen.circle_border)
    private val colorInnerCircle = ContextCompat.getColor(context, R.color.color_inner_circle)
    private val colorBorderCircle = ContextCompat.getColor(context, R.color.color_border_circle)

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
    }

    private var touchedCustomPoint: CustomPoint? = null
    private var progressTranslateArrow = 0.2f

    private var point1: CustomPoint
    private var point2: CustomPoint

    init {
        point1 = CustomPoint(context.getDisplayWidth() / 3f, context.getDisplayHeight() / 3f)
        point2 = CustomPoint(context.getDisplayWidth() / 2f, context.getDisplayHeight() / 2f)

//        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
//            override fun run() {
//                progressTranslateArrow += 0.1f
//                if (progressTranslateArrow > 0.8f) {
//                    progressTranslateArrow = 0.05f
//                }
//                postInvalidate()
//                postDelayed(this, 100)
//            }
//        }, 100)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawLineBetweenPoints(canvas)
            drawCircle(point1.x, point1.y, canvas)
            drawCircle(point2.x, point2.y, canvas)
            drawCircleOnLine(canvas)
        }
    }

    private fun drawCircleOnLine(canvas: Canvas) {
        val xA = point1.x + (point2.x - point1.x) * progressTranslateArrow
        val yA =
            ((xA - point1.x) * (point2.y - point1.y)) / (point2.x - point1.x) + point1.y
        paint.style = Paint.Style.FILL
        paint.color = Color.CYAN
        canvas.drawCircle(xA, yA, 8f, paint)
    }

    private fun drawLineBetweenPoints(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.color = colorInnerCircle
        canvas.drawLine(
            point1.x,
            point1.y,
            point2.x,
            point2.y,
            paint
        )
    }

    private fun drawCircle(posX: Float, posY: Float, canvas: Canvas) {
        paint.strokeWidth = eventCircleBorder

        /** draw inner circle */
        paint.style = Paint.Style.FILL
        paint.color = colorInnerCircle
        canvas.drawCircle(posX, posY, eventCircleRadius, paint)

        /** draw border of circle (basically is an outline circle) */
        paint.style = Paint.Style.STROKE
        paint.color = colorBorderCircle
        canvas.drawCircle(posX, posY, eventCircleRadius, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchedCustomPoint = findTouchedEvent(event)
                touchedCustomPoint != null
            }

            MotionEvent.ACTION_MOVE -> {
                touchedCustomPoint?.x = event.x
                touchedCustomPoint?.y = event.y
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                touchedCustomPoint = null
                true
            }

            else -> false
        }
    }

    private fun findTouchedEvent(motionEvent: MotionEvent): CustomPoint? {
        val x = motionEvent.x
        val y = motionEvent.y

        return if (checkValidXY(x, y, point1.x, point1.y)) {
            point1
        } else if (checkValidXY(x, y, point2.x, point2.y)) {
            point2
        } else {
            null
        }
    }

    private fun checkValidXY(x: Float, y: Float, pointX: Float, pointY: Float): Boolean {
        val xRange = pointX - eventCircleRadius to pointX + eventCircleRadius
        val yRange = pointY - eventCircleRadius to pointY + eventCircleRadius
        val validX = x > xRange.first && x < xRange.second
        val validY = y > yRange.first && y < yRange.second
        return validX && validY
    }

}