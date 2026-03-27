package com.example.peakflow.ui.profile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RadarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axes = 4
    private val maxValue = 5f
    private val gridLevels = 5
    private var values = floatArrayOf(0f, 0f, 0f, 0f)
    private val labels = arrayOf("KND", "TCH", "AKL", "RYZ")
    private val dp = context.resources.displayMetrics.density

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 1f * dp
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 0.5f * dp
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55FF8C42")
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFF8C42")
        style = Paint.Style.STROKE
        strokeWidth = 2f * dp
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFF8C42")
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AAFFFFFF")
        textSize = 11f * dp
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.1f
        isFakeBoldText = true
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFF8C42")
        textSize = 10f * dp
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    fun setStats(condition: Int, technique: Int, acclimatization: Int, risk: Int) {
        values = floatArrayOf(
            condition.toFloat(),
            technique.toFloat(),
            acclimatization.toFloat(),
            risk.toFloat()
        )
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Take full parent width, respect height constraint
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val chartSize = min(width, height)
        val radius = chartSize * 0.30f
        val labelRadius = chartSize * 0.38f

        val angles = floatArrayOf(-90f, 0f, 90f, 180f)

        // Draw grid polygons
        for (level in 1..gridLevels) {
            val path = Path()
            val r = radius * level / gridLevels
            for (i in 0 until axes) {
                val angle = Math.toRadians(angles[i].toDouble())
                val x = cx + r * cos(angle).toFloat()
                val y = cy + r * sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            canvas.drawPath(path, gridPaint)
        }

        // Draw axis lines
        for (i in 0 until axes) {
            val angle = Math.toRadians(angles[i].toDouble())
            val x = cx + radius * cos(angle).toFloat()
            val y = cy + radius * sin(angle).toFloat()
            canvas.drawLine(cx, cy, x, y, axisPaint)
        }

        // Draw value polygon
        val valuePath = Path()
        for (i in 0 until axes) {
            val angle = Math.toRadians(angles[i].toDouble())
            val r = radius * (values[i] / maxValue)
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) valuePath.moveTo(x, y) else valuePath.lineTo(x, y)
        }
        valuePath.close()
        canvas.drawPath(valuePath, fillPaint)
        canvas.drawPath(valuePath, strokePaint)

        // Draw dots at value points
        for (i in 0 until axes) {
            val angle = Math.toRadians(angles[i].toDouble())
            val r = radius * (values[i] / maxValue)
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            canvas.drawCircle(x, y, 4f * dp, dotPaint)
        }

        // Draw labels and values
        for (i in 0 until axes) {
            val angle = Math.toRadians(angles[i].toDouble())
            val lx = cx + labelRadius * cos(angle).toFloat()
            val ly = cy + labelRadius * sin(angle).toFloat()

            val labelY = when (i) {
                0 -> ly - 8f * dp
                2 -> ly + 12f * dp
                else -> ly + 4f * dp
            }

            canvas.drawText(labels[i], lx, labelY, labelPaint)

            val valueText = "${values[i].toInt()}/${maxValue.toInt()}"
            val valueY = when (i) {
                0 -> ly + 6f * dp
                2 -> ly + 24f * dp
                else -> ly + 18f * dp
            }
            canvas.drawText(valueText, lx, valueY, valuePaint)
        }
    }
}
