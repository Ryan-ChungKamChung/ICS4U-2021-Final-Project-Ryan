/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * This is the main activity for the app.
 */

package com.ryan.trivia_app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //  Gets error messages from other activities
        val extras = intent.extras
        if (extras != null) {
            if (extras.getString("error") != null) {
                Toast.makeText(this, extras.getString("error"), Toast.LENGTH_LONG).show()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentPlaceholder, MenuFragment())
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
