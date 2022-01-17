/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * This is the game activity for the app.
 */

package com.ryan.trivia_app

import android.os.Bundle
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
}
