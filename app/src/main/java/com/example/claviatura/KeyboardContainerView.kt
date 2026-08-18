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
            LayoutParams.MATCH_PARENT
        )
        addView(keyboardBodyView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hMargin = dpToPx(prefs.horizontalMargin)
        val bMargin = dpToPx(prefs.bottomMargin)
        val keyHeight = dpToPx(prefs.keyboardHeight)
        val totalHeight = keyHeight + bMargin
        setPadding(hMargin, 0, hMargin, bMargin)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    fun setKeyboardContent(view: View) {
        keyboardBodyView.removeAllViews()
        keyboardBodyView.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
