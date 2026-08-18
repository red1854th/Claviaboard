package com.example.claviatura

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class MiniKeyboardPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0A0D14")
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E293B")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E293B")
        style = Paint.Style.FILL
    }

    private val keySpecialPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#162032")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F8FAFC")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
        isFakeBoldText = true
    }

    private val textSmallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#64748B")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
    }

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val radius = 6f * resources.displayMetrics.density

        // Draw Keyboard Frame Background
        rect.set(0f, 0f, w, h)
        canvas.drawRoundRect(rect, radius, radius, bgPaint)
        canvas.drawRoundRect(rect, radius, radius, borderPaint)

        val layoutType = (tag as? String) ?: "dubeolshik"

        when (layoutType) {
            "naratgeul" -> drawNaratgeul(canvas, w, h)
            "cheonjiin", "cheonjiin_plus" -> drawCheonjiin(canvas, w, h, layoutType == "cheonjiin_plus")
            "danmoeum", "danmoeum_plus" -> drawDanmoeum(canvas, w, h, layoutType == "danmoeum_plus")
            "vega" -> drawVega(canvas, w, h)
            "dvorak" -> drawDvorak(canvas, w, h)
            "colemak" -> drawColemak(canvas, w, h)
            "workman" -> drawWorkman(canvas, w, h)
            "qwertz" -> drawQwertz(canvas, w, h)
            "azerty" -> drawAzerty(canvas, w, h)
            "qwerty" -> drawQwerty(canvas, w, h)
            else -> drawDubeolshik(canvas, w, h)
        }
    }

    private fun drawDubeolshik(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("SH", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "DEL"),
            listOf("123", "EN", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawQwerty(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("SH", "Z", "X", "C", "V", "B", "N", "M", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawDvorak(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("P", "Y", "F", "G", "C", "R", "L", "?", "/", "="),
            listOf("A", "O", "E", "U", "I", "D", "H", "T", "N", "S"),
            listOf("SH", "Q", "J", "K", "X", "B", "M", "W", "V", "Z", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawColemak(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("Q", "W", "F", "P", "G", "J", "L", "U", "Y", ";"),
            listOf("A", "R", "S", "T", "D", "H", "N", "E", "I", "O"),
            listOf("SH", "Z", "X", "C", "V", "B", "K", "M", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawWorkman(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("Q", "D", "R", "W", "B", "J", "F", "U", "P", ";"),
            listOf("A", "S", "H", "T", "G", "Y", "N", "E", "O", "I"),
            listOf("SH", "Z", "X", "M", "C", "V", "K", "L", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawQwertz(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("SH", "Y", "X", "C", "V", "B", "N", "M", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawAzerty(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("Q", "S", "D", "F", "G", "H", "J", "K", "L", "M"),
            listOf("SH", "W", "X", "C", "V", "B", "N", "DEL"),
            listOf("123", "KO", "SPACE", ".", "RET")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawDanmoeum(canvas: Canvas, w: Float, h: Float, isPlus: Boolean) {
        val rows = if (isPlus) {
            listOf(
                listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
                listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
                listOf("SH", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "DEL"),
                listOf("123", "EN", "SPACE", ".", "RET")
            )
        } else {
            listOf(
                listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅗ", "ㅐ", "ㅔ"),
                listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅓ", "ㅏ", "ㅣ"),
                listOf("SH", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅜ", "ㅡ", "DEL"),
                listOf("123", "EN", "SPACE", ".", "RET")
            )
        }
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawNaratgeul(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("ㄱ", "ㄴ", "ㅏ", "⌫"),
            listOf("ㄹ", "ㅁ", "ㅓ", "↵"),
            listOf("ㅅ", "ㅇ", "ㅣ", "+획"),
            listOf("SPACE", "ㅡ", "·", "++")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawCheonjiin(canvas: Canvas, w: Float, h: Float, isPlus: Boolean) {
        val rows = if (isPlus) {
            listOf(
                listOf("ㅣ", "·", "ㅡ", "⌫"),
                listOf("ㄱㅋ", "ㄴㄹ", "ㄷㅌ", "↵"),
                listOf("ㅂㅍ", "ㅅㅎ", "ㅈㅊ", "ㅇㅁ"),
                listOf("!?", "SPACE", ".", "123")
            )
        } else {
            listOf(
                listOf("ㅣ", "·", "ㅡ"),
                listOf("ㄱㅋ", "ㄴㄹ", "ㄷㅌ"),
                listOf("ㅂㅍ", "ㅅㅎ", "ㅈㅊ"),
                listOf("ㅇㅁ", "SPACE", "⌫")
            )
        }
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawVega(canvas: Canvas, w: Float, h: Float) {
        val rows = listOf(
            listOf("1", "2", "3", "⌫"),
            listOf("ㄱ", "ㄴ", "ㄷ", "ㅏ"),
            listOf("ㅂ", "ㅅ", "ㅇ", "ㅓ"),
            listOf("ㅋ", "ㅌ", "ㅍ", "↵")
        )
        drawGridRows(canvas, w, h, rows)
    }

    private fun drawGridRows(canvas: Canvas, w: Float, h: Float, rows: List<List<String>>) {
        val padX = 4f * resources.displayMetrics.density
        val padY = 4f * resources.displayMetrics.density
        val rowGap = 3f * resources.displayMetrics.density
        val keyGap = 3f * resources.displayMetrics.density

        val numRows = rows.size
        val availableH = h - padY * 2 - (numRows - 1) * rowGap
        val keyH = availableH / numRows

        var curY = padY

        textPaint.textSize = keyH * 0.42f
        textSmallPaint.textSize = keyH * 0.32f

        for (row in rows) {
            val numCols = row.size
            val availableW = w - padX * 2 - (numCols - 1) * keyGap
            val keyW = availableW / numCols

            var curX = padX
            for (key in row) {
                rect.set(curX, curY, curX + keyW, curY + keyH)
                val isSpecial = key in listOf("SH", "DEL", "123", "EN", "KO", "SPACE", "RET", "+획", "++", "⌫", "↵")
                val isSpace = key == "SPACE"

                val p = if (isSpecial) keySpecialPaint else keyPaint
                canvas.drawRoundRect(rect, 4f, 4f, p)

                val tPaint = if (key.length > 2) textSmallPaint else textPaint
                val fontMetrics = tPaint.fontMetrics
                val textY = rect.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2

                canvas.drawText(if (isSpace) "_" else key, rect.centerX(), textY, tPaint)
                curX += keyW + keyGap
            }
            curY += keyH + rowGap
        }
    }
}
