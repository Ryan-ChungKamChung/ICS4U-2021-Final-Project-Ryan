package com.ryan.trivia_app

import android.app.Activity
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // API call in background thread
        thread {
            // API class
            val api = API()
            // API call
            val json = api.request("https://opentdb.com/api_category.php")
            println(json)
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

                    var transferred = false
                    for (iterator in buttons.indices) {
                        buttons[iterator].setOnClickListener {
                            toGame(it as Button, categories[iterator], transferred)
                            transferred = true
                        }
                    }
                }
            } else {
                // Goes back to main and shows a Toast
                startActivity(api.internetError(requireActivity()))
                (context as Activity).overridePendingTransition(0, 0)
            }
        }
    }

    /**
     * Initiates a transition and replaces the fragment by TriviaFragment.
     *
     * @param button the button that was clicked by the user.
     */
    private fun toGame(button: Button, category: Category, transferred: Boolean) {
        // Sets chosen button to green
        button.setBackgroundResource(R.drawable.game_button_green_pressed)

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
                    .commit()
            }, 1000)
        }
    }
}
