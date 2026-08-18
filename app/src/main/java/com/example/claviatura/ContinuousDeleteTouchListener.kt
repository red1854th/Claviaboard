package com.example.claviatura

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class ContinuousDeleteTouchListener(
    private val getHoldDelay: () -> Long, // Int에서 Long으로 변경 (Preferences와 일치)
    private val onDelete: () -> Unit
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())
    private var isDeleting = false

    private val deleteRunnable = object : Runnable {
        override fun run() {
            if (isDeleting) {
                onDelete()
                handler.postDelayed(this, 50L) // 연속 삭제 간격 50ms
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDeleting = true
                onDelete() // 1회 즉시 삭제
                handler.postDelayed(deleteRunnable, getHoldDelay()) // .toLong() 제거 (이미 Long임)
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