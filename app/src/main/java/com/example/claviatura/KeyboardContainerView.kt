package com.example.claviatura

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

@SuppressLint("ViewConstructor")
class KeyboardContainerView(
    context: Context,
    private val prefs: KeyboardPreferences
) : LinearLayout(context) {

    private val keyboardBodyView: FrameLayout = FrameLayout(context)
    val topHeadroomDp: Int = 74

    init {
        orientation = VERTICAL
        gravity = Gravity.BOTTOM
        setBackgroundColor(Color.TRANSPARENT)
        clipChildren = false
        clipToPadding = false
        isClickable = true
        isFocusable = true

        keyboardBodyView.clipChildren = false
        keyboardBodyView.clipToPadding = false
        keyboardBodyView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        addView(keyboardBodyView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hMargin = dpToPx(prefs.horizontalMargin)
        val bMargin = dpToPx(prefs.bottomMargin)
        val keyHeight = dpToPx(prefs.keyboardHeight)
        val topHeadroomPx = dpToPx(topHeadroomDp)
        
        // Full container height includes the transparent top headroom for unclipped circular previews
        val totalHeight = keyHeight + bMargin + topHeadroomPx
        setPadding(hMargin, topHeadroomPx, hMargin, bMargin)
        
        val newHeightSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    fun setKeyboardContent(view: View) {
        keyboardBodyView.removeAllViews()
        keyboardBodyView.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dpToPx(prefs.keyboardHeight)
            )
        )
    }

    fun updateKeyboardContentHeight() {
        if (keyboardBodyView.childCount > 0) {
            val child = keyboardBodyView.getChildAt(0)
            child.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dpToPx(prefs.keyboardHeight)
            )
        }
        requestLayout()
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
