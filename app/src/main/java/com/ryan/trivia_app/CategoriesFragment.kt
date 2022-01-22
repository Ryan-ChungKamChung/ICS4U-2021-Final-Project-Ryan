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
                val categories = api.parseCategories(json)
                // UI thread
                requireActivity().runOnUiThread {
                    // Array of buttons
                    val buttons = arrayOf(
                        binding.btnChoice1, binding.btnChoice2,
                        binding.btnChoice3, binding.btnChoice4
                    )
                    // Binds text to buttons
                    for (iterator in buttons.indices) {
                        buttons[iterator].text = categories[iterator].name
                    }

                    /* onClickListeners for the user's choice of category.
                        Starts the transfer process to the start of the game */
                    var transferred = false
                    buttons[0].setOnClickListener {
                        transferred = toGame(it as Button, categories[0], transferred)
                    }
                    buttons[1].setOnClickListener {
                        transferred = toGame(it as Button, categories[1], transferred)
                    }
                    buttons[2].setOnClickListener {
                        transferred = toGame(it as Button, categories[2], transferred)
                    }
                    buttons[3].setOnClickListener {
                        transferred = toGame(it as Button, categories[3], transferred)
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
    private fun toGame(button: Button, category: Category, transferred: Boolean): Boolean {
        // Sets chosen button to green
        button.setBackgroundColor(Color.parseColor("#33B16F"))

        if (!transferred) {
            // Executes this code 1 second after the button was set to green
            Handler(Looper.getMainLooper()).postDelayed({
                // Adds the chosen category to the bundle to be sent to TriviaFragment
                val triviaFragment = TriviaFragment()
                val args = Bundle()
                args.putParcelable("category", category)
                triviaFragment.arguments = args

                // Replaces this fragment with TriviaFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentPlaceholder, triviaFragment)
                    .commitAllowingStateLoss()
            }, 1000)
        }

        return true
    }
}
