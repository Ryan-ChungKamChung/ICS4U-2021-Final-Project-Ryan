/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Fragment that is attached to GameActivity during the game loop.
 * The game is played in this fragment.
 */

package com.ryan.trivia_app.view

import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.R
import com.ryan.trivia_app.controller.Transfer
import com.ryan.trivia_app.controller.TriviaController
import com.ryan.trivia_app.controller.onLimitedClick
import com.ryan.trivia_app.databinding.FragmentTriviaBinding
import com.ryan.trivia_app.model.*
import kotlin.concurrent.thread

/** TriviaFragment class, this is the in-game loop. */
class TriviaFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentTriviaBinding? = null

    /** Binding getter. */
    private val binding get() = _binding!!

    /**
     * After the view is created with binding, create onClickListeners
     *
     * @param view the view with the inflated layout
     * @param savedInstanceState Bundle holding instanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val triviaController = TriviaController(binding, requireActivity(), requireContext())

        // Gets the passed data from CategoriesFragment
        val category = requireArguments().getParcelable<Category>("category")
        // Sets the category as the top bar
        binding.txtCategory.text = category!!.name

        // FX setting in persistent storage
        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val fx = settings.getBoolean("fx", true)

        // Array of buttons
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2,
            binding.btnAnswer3, binding.btnAnswer4
        )

        val questionsArray = TriviaAPIRequest(
            "https://opentdb.com/api.php?amount=50&category=" + category.id + "&type=multiple"
        ).execute()
        .get()

        // Makes sure the API call was successful
        if (questionsArray != null) {
            // Game attributes
            // Numbers of questions that have passed
            var questionCount = 0
            // Lives remaining
            var lives = 3

            // Current question
            var question = Question("", "", "", "", "")
            try {
                question = questionsArray[questionCount]
            } catch (e: IndexOutOfBoundsException) {
                Transfer().transferForIssue(requireActivity())
            }

            // Show first question
            triviaController.showQuestion(questionsArray[questionCount], questionCount)

            // Binds onClickListeners to each of the buttons
            buttons.forEach { it ->
                // onClickListeners with a time limit to prevent spam
                it.onLimitedClick {
                    it as Button

                    // If user won
                    if (questionCount == 49) {
                        /* Add 1 to questionCount as we want the amount
                           of questions answered, not the index of the array */
                        triviaController.showEndOfGame(true, ++questionCount)
                    } else {
                        // Checks if the user was right
                        val isCorrect = it.text == question.rightAnswer

                        // Disables button presses of other buttons
                        buttons.forEach { button -> button.isEnabled = false }
                        // Shows visually the right and wrong answers
                        triviaController.showAnswers(it, question.rightAnswer, isCorrect)

                        // Reassigns the next question
                        question = questionsArray[++questionCount]
                        // If user was wrong
                        if (isCorrect) {
                            // Play right answer sound effect
                            if (fx) {
                                thread {
                                    val rightSound =
                                        MediaPlayer.create(requireContext(), R.raw.right)
                                    rightSound.setVolume(1.5f, 1.5f)
                                    rightSound.start()
                                }
                            }

                            // Show new question
                            triviaController.newQuestion(buttons, question, questionCount)
                        } else {
                            // Play wrong answer sound effect
                            if (fx) {
                                thread {
                                    val wrongSound =
                                        MediaPlayer.create(requireContext(), R.raw.wrong)
                                    wrongSound.setVolume(1.5f, 1.5f)
                                    wrongSound.start()
                                }
                            }

                            // Removes a life
                            --lives
                            // Updates life indicator
                            triviaController.showLives(lives)

                            // Checks whether to end the game or show the next question
                            if (lives == 0) {
                                // Minus 3 to questionCount to remove 3 wrong answers
                                triviaController.showEndOfGame(false, questionCount - 3)
                            } else {
                                // Shows next question
                                triviaController.newQuestion(buttons, question, questionCount)
                            }
                        }
                    }
                }
            }
        } else {
            /*
                Goes back to MainActivity and shows a Toast
                in case there are internet connection issues.
            */
            Transfer().transferForIssue(requireActivity())
        }
    }

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
        _binding = FragmentTriviaBinding.inflate(layoutInflater)
        return binding.root
    }
}
