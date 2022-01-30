package com.ryan.trivia_app.controller

import android.view.View
import android.view.Window

class UIModification {
    @Suppress("DEPRECATION")
    fun fullScreen(window: Window) {
        // Removes top and bottom system bars
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN
        /* Makes it so user clicks don't bring back the bars.
           Minimum is API 19 so checking isn't needed */
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}
