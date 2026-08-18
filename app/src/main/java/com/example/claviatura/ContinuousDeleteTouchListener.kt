package com.example.claviatura

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class ContinuousDeleteTouchListener(
    private val getHoldDelay: () -> Long,
    private val getRepeatInterval: () -> Long = { 45L },
    private val onDelete: () -> Unit
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())
    private var isDeleting = false

    private val deleteRunnable = object : Runnable {
        override fun run() {
            if (isDeleting) {
                onDelete()
                handler.postDelayed(this, getRepeatInterval())
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDeleting = true
                onDelete()
                handler.postDelayed(deleteRunnable, getHoldDelay())
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDeleting = false
                handler.removeCallbacks(deleteRunnable)
                return true
            }
        }
        return false
    }
}
