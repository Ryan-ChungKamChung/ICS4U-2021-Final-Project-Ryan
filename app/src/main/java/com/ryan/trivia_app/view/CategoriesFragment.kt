/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Fragment that is attached to GameActivity pre-game, where the user gets to choose a category
 * inside the trivia API.
 */

package com.ryan.trivia_app.view

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.R
import com.ryan.trivia_app.controller.Transfer
import com.ryan.trivia_app.databinding.FragmentCategoriesBinding
import com.ryan.trivia_app.model.API
import com.ryan.trivia_app.model.Category
import kotlin.concurrent.thread

/** CategoriesFragment class, the user gets to choose a category inside the trivia API. */
class CategoriesFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentCategoriesBinding? = null

    /** Binding getter. */
    private val binding get() = _binding!!

    /**
     * When the view is created.
     *
     * @param inflater XML reader.
     * @param container contains other views.
     * @param savedInstanceState Bundle holding instanceState.
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

    /**
     * After the view is created with binding, create onClickListeners
     *
     * @param view the view with the inflated layout
     * @param savedInstanceState Bundle holding instanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // API call in background thread
        thread {
            // API class
            val api = API()
            // API call
            val json = api.request("https://opentdb.com/api_category.php")
            // TODO fix threading issue
            println(json)
            // Makes sure the app doesn't crash due to missing internet connection or parsing issues
            if (json != null) {
                val buttons = arrayOf(
                    binding.btnChoice1, binding.btnChoice2,
                    binding.btnChoice3, binding.btnChoice4
                )
                // UI thread
                requireActivity().runOnUiThread {
                    // Returned categories from the API call
                    val categories = api.parseCategories(json)
                    println(categories)
                    // Binds text to buttons
                    for (iterator in buttons.indices) {
                        buttons[iterator].text = categories[iterator].name
                    }

                    /*
                        Make sure the app doesn't crash if the user
                        spams the button and the transfer process has started.
                     */
                    var transferred = false
                    // setOnClickListener for each of the buttons
                    for (iterator in buttons.indices) {
                        buttons[iterator].setOnClickListener {
                            // Transitions to TriviaFragment with the chosen category being passed
                            if (!transferred) {
                                toGame(it as Button, categories[iterator])
                            }
                            transferred = true
                        }
                    }
                }
            } else {
                /*
                    Goes back to MainActivity and shows a Toast
                    in case there are internet connection issues.
                 */
                Transfer().transferForIssue(requireActivity())
                (context as Activity).overridePendingTransition(0, 0)
            }
        }
    }

    /**
     * Initiates a transition and replaces the fragment by TriviaFragment.
     *
     * @param button the button that was clicked by the user.
     */
    private fun toGame(button: Button, category: Category) {
        // Sets chosen button to green
        button.setBackgroundResource(R.drawable.game_button_green_pressed)

        // Executes this code 1 second after the button was set to green
        Handler(Looper.getMainLooper()).postDelayed({
            // Adds the chosen category to the bundle to be sent to TriviaFragment
            val args = Bundle()
            args.putParcelable("category", category)

            val triviaFragment = TriviaFragment()
            triviaFragment.arguments = args

            // Replaces this fragment with TriviaFragment
            Transfer().transferToFragment(
                parentFragmentManager, R.id.fragmentPlaceholder, triviaFragment
            )
        }, 1000)
    }
}
