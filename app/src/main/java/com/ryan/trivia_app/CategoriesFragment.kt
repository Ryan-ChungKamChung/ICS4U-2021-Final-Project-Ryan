package com.ryan.trivia_app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.databinding.FragmentCategoriesBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class CategoriesFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentCategoriesBinding? = null
    /** Binding getter. */
    private val binding get() = _binding!!

    /**
     * When the view is created.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view for binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        // https://opentdb.com/api_category.php
        val categoriesArray = CallAPI().callCategories(requireActivity())
//        val buttons = arrayOf(binding.btnChoice1, binding.btnChoice2, binding.btnChoice3, binding.btnChoice4)
//        for (iterator in 0  until buttons.count()) {
//            val randomNumber = (0..categoriesArray.count()).random()
//            buttons[iterator].text = categoriesArray[randomNumber].name
//            categoriesArray.removeAt(randomNumber)
//        }
        // All possible categories
        val categories = arrayOf(
            "Any Category", "General Knowledge", "Books", "Film", "Music", "Musicals & Theaters",
            "Television", "Video Games", "Board Games", "Science & Nature", "Computers", "Math",
            "Mythology", "Sports", "Geography", "History", "Politics", "Art", "Celebrities",
            "Animals", "Vehicles", "Comics", "Gadgets", "Anime & Manga", "Cartoon & Animations"
        )

//        // Chooses random categories and sets them on the 4 choice buttons
        binding.btnChoice1.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice2.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice3.text = categories[Random.nextInt(0, 25)]
        binding.btnChoice4.text = categories[Random.nextInt(0, 25)]

        /* onClickListeners for the user's choice of category.
           Starts the transfer process to the start of the game */
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

    /**
     * Initiates a transition and replaces the fragment by TriviaFragment.
     *
     * @param button the button that was clicked by the user.
     */
    private fun transferToQuizFragment(button: Button) {
        // Sets chosen button to green
        button.setBackgroundColor(Color.parseColor("#33B16F"))

        // Executes this code 1 second after the button was set to green
        Handler(Looper.getMainLooper()).postDelayed({
            // Adds the chosen category to the bundle to be sent to TriviaFragment
            val args = Bundle()
            args.putString("category", button.text.toString())
            val triviaFragment = TriviaFragment()
            triviaFragment.arguments = args

            // Replaces this fragment with TriviaFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, triviaFragment)
                .commit()
        }, 1000)
    }
}
