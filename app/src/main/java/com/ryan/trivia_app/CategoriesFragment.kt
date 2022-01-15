package com.ryan.trivia_app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ryan.trivia_app.databinding.FragmentCategoriesBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        val categories = arrayOf(
            "Any Category", "General Knowledge", "Books", "Film", "Music", "Musicals & Theaters",
            "Television", "Video Games", "Board Games", "Science & Nature", "Computers", "Math",
            "Mythology", "Sports", "Geography", "History", "Politics", "Art", "Celebrities",
            "Animals", "Vehicles", "Comics", "Gadgets", "Anime & Manga", "Cartoon & Animations"
        )

        binding.btnChoice1.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice2.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice3.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice4.text = categories[Random.nextInt(0, 25)]

        binding.btnChoice1.setOnClickListener {
            transferToQuizFragment(binding.btnChoice1)
        }

        binding.btnChoice2.setOnClickListener {
            transferToQuizFragment(binding.btnChoice2)
        }

        binding.btnChoice3.setOnClickListener {
            transferToQuizFragment(binding.btnChoice3)
        }

        binding.btnChoice4.setOnClickListener {
            transferToQuizFragment(binding.btnChoice4)
        }

        return binding.root
    }

    private fun transferToQuizFragment(button: Button) {

        button.setBackgroundColor(Color.parseColor("#33B16F"))

        Handler(Looper.getMainLooper()).postDelayed({
            val args = Bundle()
            args.putString("category", button.text.toString())

            val triviaFragment = TriviaFragment()
            triviaFragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, triviaFragment)
                .commit()
        }, 1000)
    }
}