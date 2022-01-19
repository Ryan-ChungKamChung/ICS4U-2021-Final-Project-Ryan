/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * This is the game activity for the app.
 */

package com.ryan.trivia_app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ryan.trivia_app.databinding.ActivityGameBinding

/** GameActivity class. All game content will run on top of this activity. */
class GameActivity : AppCompatActivity() {
    /**
     * When the activity is created on screen.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Replaces placeholder FrameLayout with CategoriesFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentPlaceholder, CategoriesFragment())
            .commit()
    }

    // Will fix later if needed, deprecation here isn't much of a concern
    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()

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
