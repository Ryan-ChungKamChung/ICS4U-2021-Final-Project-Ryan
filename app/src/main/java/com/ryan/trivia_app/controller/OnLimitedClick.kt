package com.ryan.trivia_app.controller

import android.os.SystemClock
import android.view.View
import android.widget.Button

class OnLimitedClick(
    private val onLimitedClick: (View) -> Unit
) : View.OnClickListener {

    private var interval = 1000
    private var lastClick: Long = 0

    override fun onClick(button: View) {
        if (interval > SystemClock.elapsedRealtime() - lastClick) {
            return
        }
        lastClick = SystemClock.elapsedRealtime()
        onLimitedClick(button)
    }
}

fun Button.onLimitedClick(onLimitedClick: (View) -> Unit) =
    setOnClickListener(OnLimitedClick { onLimitedClick(it) })
