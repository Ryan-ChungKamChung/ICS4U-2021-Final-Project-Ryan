package com.ryan.trivia_app.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ryan.trivia_app.R
import com.ryan.trivia_app.controller.Transfer
import com.ryan.trivia_app.controller.onLimitedClick
import com.ryan.trivia_app.databinding.FragmentTriviaBinding
import com.ryan.trivia_app.model.API
import com.ryan.trivia_app.model.Category
import com.ryan.trivia_app.model.Question
import kotlin.concurrent.thread
import org.json.JSONArray
import org.json.JSONException

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

        // Gets the passed data from CategoriesFragment
        val category = requireArguments().getParcelable<Category>("category")
        // Sets the category as the top bar
        binding.txtCategory.text = category!!.name

        // FX setting in persistent storage
        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val fx = settings.getBoolean("fx", true)

        // API call in background thread
        thread {
            // API call
            val json = API().request(
                "https://opentdb.com/api.php?amount=50&category=" + category.id + "&type=multiple"
            )
            // Array of buttons
            val buttons = arrayOf(
                binding.btnAnswer1,
                binding.btnAnswer2,
                binding.btnAnswer3,
                binding.btnAnswer4
            )
            // UI thread
            requireActivity().runOnUiThread {
                // Makes sure the API call was successful
                if (json != null) {
                    // Parsed array of Questions
                    val questionsArray = API().parseQuestions(json)

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
                    showQuestion(binding, questionsArray[questionCount], questionCount)

                    // Binds onClickListeners to each of the buttons
                    buttons.forEach { it ->
                        // onClickListeners with a time limit to prevent spam
                        it.onLimitedClick {
                            it as Button

                            // If user won
                            if (questionCount == 49) {
                                /* Add 1 to questionCount as we want the amount
                                   of questions answered, not the index of the array */
                                showEndOfGame(binding, true, ++questionCount)
                            } else {
                                // Checks if the user was right
                                val isCorrect = it.text == question.rightAnswer

                                // Disables button presses of other buttons
                                buttons.forEach { button -> button.isEnabled = false }
                                // Shows visually the right and wrong answers
                                showAnswers(binding, it, question.rightAnswer, isCorrect)

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
                                    newQuestion(binding, buttons, question, questionCount)
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
                                    showLives(binding, lives)

                                    // Checks whether to end the game or show the next question
                                    if (lives == 0) {
                                        // Minus 3 to questionCount to remove 3 wrong answers
                                        showEndOfGame(binding, false, questionCount - 3)
                                    } else {
                                        // Shows next question
                                        newQuestion(binding, buttons, question, questionCount)
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

    /**
     * Binds the question to the view.
     *
     * @param binding access to the fragment's views.
     * @param question the current question to bind.
     * @param questionCount how many questions the user has answered.
     */
    private fun showQuestion(
        binding: FragmentTriviaBinding,
        question: Question,
        questionCount: Int
    ) {
        // Shuffles the answers
        val answers = arrayOf(
            question.rightAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        )
        answers.shuffle()

        // Array of buttons
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        // Binds the answers to the buttons' text
        for (iterator in buttons.indices) {
            buttons[iterator].text = answers[iterator]
        }

        // Updates the question indicator
        "Question ${questionCount + 1}".also { binding.txtQuestionCount.text = it }
        // Updates the question
        binding.txtQuestion.text = question.question
    }

    /**
     * Shows the right and wrong answers visually to the user.
     *
     * @param binding access to the fragment's views.
     * @param button the button that the user pressed.
     * @param rightAnswer the right answer.
     * @param userRight is the user right.
     */
    private fun showAnswers(
        binding: FragmentTriviaBinding,
        button: Button,
        rightAnswer: String,
        userRight: Boolean,
    ) {
        // Array of buttons
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )

        // If the user is right, make the button green
        if (userRight) {
            button.setBackgroundResource(R.drawable.game_button_green_pressed)
        } else {
            // If the user is wrong, make the button red
            button.setBackgroundResource(R.drawable.game_button_red)

            // Finds the right answer within the buttons and delete
            for (buttonElement: Button in buttons) {
                if (buttonElement.text == rightAnswer) {
                    buttonElement.setBackgroundResource(R.drawable.game_button_green_unpressed)
                }
            }
        }
    }

    /**
     * Binds the new question to the fragment.
     *
     * @param binding access to the fragment's views.
     * @param buttons array of buttons.
     * @param question the new question to bind.
     * @param questionCount how many questions the user has answered.
     */
    private fun newQuestion(
        binding: FragmentTriviaBinding,
        buttons: Array<Button>,
        question: Question,
        questionCount: Int
    ) {
        // 1 second delay until next question is shown
        Handler(Looper.getMainLooper()).postDelayed({
            // Set back default drawable
            binding.btnAnswer1.setBackgroundResource(R.drawable.default_button)
            binding.btnAnswer2.setBackgroundResource(R.drawable.default_button)
            binding.btnAnswer3.setBackgroundResource(R.drawable.default_button)
            binding.btnAnswer4.setBackgroundResource(R.drawable.default_button)
            // Show new question
            showQuestion(binding, question, questionCount)
            // Return ability to click buttons
            buttons.forEach { button -> button.isEnabled = true }
        }, 1000)
    }

    /**
     * Show the end of game screen to the user.
     *
     * @param binding access to the fragment's views.
     * @param win whether the user won or not.
     * @param score the number of right answers.
     */
    private fun showEndOfGame(binding: FragmentTriviaBinding, win: Boolean, score: Int) {
        // 1 second delay until end of game is shown
        Handler(Looper.getMainLooper()).postDelayed({
            // Makes end of game screen visible
            binding.endOfGame.visibility = View.VISIBLE
            // Sets the header to if the user won or not
            binding.txtWinOrLose.text = if (win) "You won!" else "Game Over"
            // Display score
            binding.txtScore.text = getString(R.string.score, score)
            // Update leaderboard in persistent storage
            updateLeaderboard(score)

            // Button to go back to MainActivity
            binding.btnMainMenu.setOnClickListener {
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
        }, 1000)
    }

    /**
     * Updates the life indicator.
     *
     * @param binding access to the fragment's views.
     * @param lives the amount of lives left.
     */
    private fun showLives(binding: FragmentTriviaBinding, lives: Int) = when (lives) {
        2 -> binding.life1.setBackgroundResource(R.drawable.life_circle_lost)
        1 -> binding.life2.setBackgroundResource(R.drawable.life_circle_lost)
        else -> binding.life3.setBackgroundResource(R.drawable.life_circle_lost)
    }

    /**
     * Updates the leaderboard inside of persistent storage.
     *
     * @param score  the number of right answers.
     */
    private fun updateLeaderboard(score: Int) {
        // Leaderboard in persistent storage
        val prefLeaderboard = PreferenceManager.getDefaultSharedPreferences(requireContext())
        // JSONArray from the String in persistent storage
        val jsonLeaderboard = try {
            JSONArray(prefLeaderboard.getString("jsonLeaderboard", "empty"))
        } catch (e: JSONException) {
            null
        }

        // Creates an ArrayList from the JSONArray
        val leaderboard = ArrayList<Int>()
        if (jsonLeaderboard != null) {
            for (iterator in 0 until jsonLeaderboard.length()) {
                leaderboard.add(jsonLeaderboard.getInt(iterator))
            }
        }

        // Sorts and removes any extra entries so the size of the leaderboard doesn't exceed 10
        leaderboard.add(score)
        leaderboard.sort()
        if (leaderboard.size > 10) {
            leaderboard.remove(11)
        }

        // Applies it to persistent storage
        val edit = prefLeaderboard.edit()
        edit.putString("jsonLeaderboard", JSONArray(leaderboard).toString())
        edit.apply()

        println(leaderboard)
        println(score)
    }
}
