/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * This is the main activity for the app.
 */

package com.ryan.trivia_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ryan.trivia_app.databinding.ActivityMainBinding

/** MainActivity class. */
class MainActivity : AppCompatActivity() {
    /**
     * When the activity is created on screen.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Gets error messages from other activities
        val extras = intent.extras
        if (extras != null) {
            Toast.makeText(this, extras.getString("error"), Toast.LENGTH_LONG).show()
        }

        // onClickListener to start the game
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, MenuActivity::class.java).putExtra(
                "settings", true
                )
            )
        }

        binding.btnLeaderboard.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, MenuActivity::class.java).putExtra(
                    "leaderboard", true
                )
            )
        }
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
