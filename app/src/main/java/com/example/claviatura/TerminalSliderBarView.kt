package com.example.claviatura

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TerminalSliderBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var label: String = ""
        set(value) { field = value; invalidate() }

    var unit: String = ""
        set(value) { field = value; invalidate() }

    var minValue: Int = 0
        set(value) { field = value; invalidate() }

    var maxValue: Int = 100
        set(value) { field = value; invalidate() }

    var currentValue: Int = 50
        set(value) {
            field = value.coerceIn(minValue, maxValue)
            invalidate()
        }

    var stepSize: Int = 1
    var showPercent: Boolean = false
    var accentColor: Int = Color.parseColor("#38BDF8")

    var onValueChanged: ((Int) -> Unit)? = null

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = dpToPx(11f)
        color = Color.parseColor("#94A3B8")
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = dpToPx(11f)
        isFakeBoldText = true
        color = Color.parseColor("#38BDF8")
    }

    private val microPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = dpToPx(8.5f)
        color = Color.parseColor("#64748B")
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#080C14")
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1f)
        color = Color.parseColor("#1E293B")
    }

    private val activeFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#1E293B")
    }

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1f)
        color = Color.parseColor("#334155")
    }

    private val majorTickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1.5f)
        color = Color.parseColor("#475569")
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4ADE80")
    }

    private val needleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(2f)
        color = Color.parseColor("#38BDF8")
    }

    private val btnTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = dpToPx(13f)
        isFakeBoldText = true
        color = Color.parseColor("#38BDF8")
        textAlign = Paint.Align.CENTER
    }

    private val minusBtnRect = RectF()
    private val plusBtnRect = RectF()
    private val trackRect = RectF()

    private var isDraggingTrack = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeight = dpToPx(62f).toInt()
        val height = resolveSize(minHeight, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // 1. Top Editorial Header Row
        labelPaint.textAlign = Paint.Align.LEFT
        val topY = dpToPx(13f)
        canvas.drawText("PARAM // " + label.uppercase(), 0f, topY, labelPaint)

        val pct = if (maxValue > minValue) {
            ((currentValue - minValue).toFloat() / (maxValue - minValue) * 100).toInt()
        } else 0

        val displayVal = if (showPercent) {
            "VAL: ${currentValue}${unit} [${pct}%]"
        } else {
            "VAL: ${currentValue}${unit}"
        }

        valuePaint.color = accentColor
        valuePaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(displayVal, w, topY, valuePaint)

        // 2. Middle Analog Caliper Gauge Row
        val btnWidth = dpToPx(32f)
        val rowTop = dpToPx(20f)
        val rowBottom = rowTop + dpToPx(24f)
        val corner = dpToPx(3f)

        minusBtnRect.set(0f, rowTop, btnWidth, rowBottom)
        plusBtnRect.set(w - btnWidth, rowTop, w, rowBottom)
        trackRect.set(btnWidth + dpToPx(6f), rowTop, w - btnWidth - dpToPx(6f), rowBottom)

        // Draw Left [-] button
        canvas.drawRoundRect(minusBtnRect, corner, corner, bgPaint)
        canvas.drawRoundRect(minusBtnRect, corner, corner, borderPaint)
        val btnTextY = rowTop + (rowBottom - rowTop) / 2f + dpToPx(4.5f)
        btnTextPaint.color = accentColor
        canvas.drawText("-", minusBtnRect.centerX(), btnTextY, btnTextPaint)

        // Draw Right [+] button
        canvas.drawRoundRect(plusBtnRect, corner, corner, bgPaint)
        canvas.drawRoundRect(plusBtnRect, corner, corner, borderPaint)
        canvas.drawText("+", plusBtnRect.centerX(), btnTextY, btnTextPaint)

        // Draw Analog Ruler Track Background & Frame
        canvas.drawRoundRect(trackRect, corner, corner, bgPaint)
        canvas.drawRoundRect(trackRect, corner, corner, borderPaint)

        val ratio = if (maxValue > minValue) {
            (currentValue - minValue).toFloat() / (maxValue - minValue)
        } else 0f

        val innerPad = dpToPx(3f)
        val trackInnerW = trackRect.width() - innerPad * 2
        val trackInnerH = trackRect.height() - innerPad * 2

        // Draw filled progress region
        val fillWidth = ratio * trackInnerW
        if (fillWidth > 0) {
            val fillRect = RectF(
                trackRect.left + innerPad,
                trackRect.top + innerPad,
                trackRect.left + innerPad + fillWidth,
                trackRect.bottom - innerPad
            )
            activeFillPaint.color = Color.parseColor("#152338")
            canvas.drawRoundRect(fillRect, dpToPx(2f), dpToPx(2f), activeFillPaint)
        }

        // Draw Analog Ruler Ticks
        val numTicks = 20
        val tickStep = trackInnerW / numTicks
        for (i in 0..numTicks) {
            val tickX = trackRect.left + innerPad + i * tickStep
            val isMajor = (i % 5 == 0)
            val tickTop = if (isMajor) trackRect.top + dpToPx(4f) else trackRect.top + dpToPx(8f)
            val tickBottom = if (isMajor) trackRect.bottom - dpToPx(4f) else trackRect.bottom - dpToPx(8f)
            canvas.drawLine(tickX, tickTop, tickX, tickBottom, if (isMajor) majorTickPaint else tickPaint)
        }

        // Draw Precision Analog Needle / Indicator
        val cursorX = trackRect.left + innerPad + fillWidth
        val cursorHalfW = dpToPx(2.5f)
        needleStrokePaint.color = accentColor
        canvas.drawLine(cursorX, trackRect.top + dpToPx(2f), cursorX, trackRect.bottom - dpToPx(2f), needleStrokePaint)

        // Center illuminated jewel dot
        val dotRadius = dpToPx(3f)
        val dotCenterY = trackRect.centerY()
        needlePaint.color = Color.parseColor("#4ADE80")
        canvas.drawCircle(cursorX, dotCenterY, dotRadius, needlePaint)

        // 3. Bottom Dense Micro-Metadata Row
        val bottomY = h - dpToPx(3f)
        microPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("MIN: ${minValue}${unit}", trackRect.left, bottomY, microPaint)

        microPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("GAUGE: CALIBRATED", trackRect.centerX(), bottomY, microPaint)

        microPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("MAX: ${maxValue}${unit}", trackRect.right, bottomY, microPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (minusBtnRect.contains(x, y)) {
                    decrement()
                    return true
                } else if (plusBtnRect.contains(x, y)) {
                    increment()
                    return true
                } else if (trackRect.contains(x, y) || (x >= minusBtnRect.right && x <= plusBtnRect.left)) {
                    isDraggingTrack = true
                    updateFromTrackTouch(x)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDraggingTrack) {
                    updateFromTrackTouch(x)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDraggingTrack) {
                    isDraggingTrack = false
                    updateFromTrackTouch(x)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun decrement() {
        val newValue = (currentValue - stepSize).coerceAtLeast(minValue)
        if (newValue != currentValue) {
            currentValue = newValue
            onValueChanged?.invoke(currentValue)
        }
    }

    private fun increment() {
        val newValue = (currentValue + stepSize).coerceAtMost(maxValue)
        if (newValue != currentValue) {
            currentValue = newValue
            onValueChanged?.invoke(currentValue)
        }
    }

    private fun updateFromTrackTouch(touchX: Float) {
        val innerLeft = trackRect.left + dpToPx(3f)
        val innerRight = trackRect.right - dpToPx(3f)
        val clampedX = touchX.coerceIn(innerLeft, innerRight)
        val ratio = (clampedX - innerLeft) / (innerRight - innerLeft).coerceAtLeast(1f)
        val calculated = minValue + (ratio * (maxValue - minValue)).toInt()
        val stepped = Math.round(calculated.toFloat() / stepSize) * stepSize
        val finalVal = stepped.coerceIn(minValue, maxValue)

        if (finalVal != currentValue) {
            currentValue = finalVal
            onValueChanged?.invoke(currentValue)
        }
    }

    private fun dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
}
