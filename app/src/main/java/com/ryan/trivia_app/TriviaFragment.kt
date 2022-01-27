package com.ryan.trivia_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.databinding.FragmentTriviaBinding
import java.lang.IndexOutOfBoundsException
import kotlin.concurrent.thread

/** TriviaFragment class, this is the in-game loop. */
class TriviaFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentTriviaBinding? = null
    /** Binding getter. */
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the passed data from CategoriesFragment
        val category = requireArguments().getParcelable<Category>("category")
        // Sets the category as the top bar
        binding.txtCategory.text = category!!.name

        thread {
            val json = API().request(
                "https://opentdb.com/api.php?amount=50&category=" + category.id + "&type=multiple"
            )
            println(json)
            val buttons = arrayOf(
                binding.btnAnswer1,
                binding.btnAnswer2,
                binding.btnAnswer3,
                binding.btnAnswer4
            )
            requireActivity().runOnUiThread {
                if (json != null) {
                    val questionsArray = API().parseQuestions(json)

                    var questionCount = 0
                    var lives = 3
                    var answered = false

                    var question = Question("", "", "", "", "")
                    try {
                        question = questionsArray[questionCount]
                    } catch (e: IndexOutOfBoundsException) {
                        startActivity(API().internetError(requireActivity()))
                    }

                    // Show first question
                    showQuestion(binding, questionsArray[questionCount])

                    buttons.forEach { it ->
                        it.setOnClickListener {
                            it as Button

                            if (questionCount == 49) {
                                /* Add 1 to questionCount as we want the amount
                                   of questions answered, not the index of the array */
                                showEndOfGame(binding, true, ++questionCount)
                            }
                            if (!answered) {
                                val isCorrect = it.text == question.rightAnswer

                                showAnswers(binding, it, question.rightAnswer, isCorrect)
                                answered = true
                                question = questionsArray[++questionCount]

                                if (!isCorrect) {
                                    if (--lives == 0) {
                                        // Minus 3 to questionCount to remove 3 wrong answers
                                        showEndOfGame(binding, false, questionCount - 3)
                                    } else {
                                        newQuestion(binding, question, answered)
                                        answered = false
                                    }
                                } else {
                                    newQuestion(binding, question, answered)
                                    answered = false
                                }
                            }
                        }
                    }
                } else {
                    // Goes back to main and shows a Toast
                    startActivity(API().internetError(requireActivity()))
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

    private fun showQuestion(binding: FragmentTriviaBinding, question: Question) {
        val answers = arrayOf(
            question.rightAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        )
        answers.shuffle()
        binding.txtQuestion.text = question.question
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        for (iterator in buttons.indices) {
            buttons[iterator].text = answers[iterator]
        }
    }

    private fun showAnswers(
        binding: FragmentTriviaBinding,
        button: Button,
        rightAnswer: String,
        userRight: Boolean,
    ) {
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        if (!userRight) {
            button.setBackgroundColor(Color.parseColor("#D84761"))
        }
        for (buttonElement: Button in buttons) {
            if (buttonElement.text == rightAnswer) {
                buttonElement.setBackgroundColor(Color.parseColor("#33B16F"))
            }
        }
    }

    private fun newQuestion(
        binding: FragmentTriviaBinding,
        question: Question,
        answered: Boolean
    ) {
        if (answered) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.btnAnswer1.setBackgroundResource(R.drawable.game_button)
                binding.btnAnswer2.setBackgroundResource(R.drawable.game_button)
                binding.btnAnswer3.setBackgroundResource(R.drawable.game_button)
                binding.btnAnswer4.setBackgroundResource(R.drawable.game_button)
                showQuestion(binding, question)
            }, 1000)
        }
    }

    private fun showEndOfGame(binding: FragmentTriviaBinding, win: Boolean, score: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.endOfGame.visibility = View.VISIBLE
            binding.txtWinOrLose.text = if (win) { "You won!" } else { "Game Over" }
            binding.txtScore.text = getString(R.string.score, score)

            binding.btnMainMenu.setOnClickListener {
                startActivity(Intent(context, MainActivity::class.java))
            }
        }, 1000)
    }
}
