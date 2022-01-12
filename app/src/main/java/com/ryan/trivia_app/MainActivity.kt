/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * This is the main activity for the app.
 */

package com.ryan.trivia_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/** MainActivity class. */
class MainActivity : AppCompatActivity() {
    /**
     * When the activity is created on screen.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}