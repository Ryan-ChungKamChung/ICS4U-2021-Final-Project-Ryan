package com.ryan.trivia_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ryan.trivia_app.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Gets error messages from other activities
        val extras = intent.extras
        if (extras != null) {
            if (extras.getBoolean("leaderboard")) {
                Toast.makeText(this, "Leaderboard", Toast.LENGTH_SHORT).show()
            } else if (extras.getBoolean("settings")) {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
        }
    }
}