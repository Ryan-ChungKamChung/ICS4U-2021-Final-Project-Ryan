package com.ryan.trivia_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ryan.trivia_app.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentPlaceholder, CategoriesFragment())
            .commit()
    }
}