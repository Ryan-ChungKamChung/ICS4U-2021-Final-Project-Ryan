package com.ryan.trivia_app

import android.app.Activity
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
import kotlin.concurrent.thread

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
        // API call in background thread
        thread {
            // API class
            val api = API()
            // API call
            val json = api.request("https://opentdb.com/api_category.php")
            // Makes sure the app doesn't crash due to internet connection
            // missing, or parsing issues
            if (json != null) {
                val usedCategories = api.parseCategories(json)
                // UI thread
                requireActivity().runOnUiThread {
                    // Array of buttons
                    val allChoiceButtons = arrayOf(
                        binding.btnChoice1, binding.btnChoice2,
                        binding.btnChoice3, binding.btnChoice4
                    )
                    // Binds text to buttons
                    for (iterator in allChoiceButtons.indices) {
                        allChoiceButtons[iterator].text = usedCategories[iterator].name
                    }

                    /* onClickListeners for the user's choice of category.
                        Starts the transfer process to the start of the game */
                    binding.btnChoice1.setOnClickListener {
                        transferToQuizFragment(binding.btnChoice1, usedCategories[0])
                    }
                    binding.btnChoice2.setOnClickListener {
                        transferToQuizFragment(binding.btnChoice2, usedCategories[1])
                    }
                    binding.btnChoice3.setOnClickListener {
                        transferToQuizFragment(binding.btnChoice3, usedCategories[2])
                    }
                    binding.btnChoice4.setOnClickListener {
                        transferToQuizFragment(binding.btnChoice4, usedCategories[3])
                    }
                }
            } else {
                // Goes back to main and shows a Toast
                startActivity(api.internetError(requireActivity()))
                (context as Activity).overridePendingTransition(0, 0)
            }
        }

        return binding.root
    }

    /**
     * Initiates a transition and replaces the fragment by TriviaFragment.
     *
     * @param button the button that was clicked by the user.
     */
    private fun transferToQuizFragment(button: Button, category: Category) {
        // Sets chosen button to green
        button.setBackgroundColor(Color.parseColor("#33B16F"))

        // Executes this code 1 second after the button was set to green
        Handler(Looper.getMainLooper()).postDelayed({
            // Adds the chosen category to the bundle to be sent to TriviaFragment
            val args = Bundle()
            args.putParcelable("category", category)
            val triviaFragment = TriviaFragment()
            triviaFragment.arguments = args

            // Replaces this fragment with TriviaFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, triviaFragment)
                .commit()
        }, 1000)
    }
}
