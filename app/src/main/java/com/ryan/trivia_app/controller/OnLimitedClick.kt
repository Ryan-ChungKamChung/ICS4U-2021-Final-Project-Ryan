/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Class to prevent repeated clicks from the user.
 */

package com.ryan.trivia_app.controller

import android.os.SystemClock
import android.view.View
import android.widget.Button

/**
 * Class to prevent repeated clicks from the user.
 *
 * @property onLimitedClick transmitted onClick.
 */
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

/**
 * Lambda extension for onLimitedClick.
 *
 * @param onLimitedClick click limiter.
 */
fun Button.onLimitedClick(onLimitedClick: (View) -> Unit) =
    setOnClickListener(OnLimitedClick { onLimitedClick(it) })
