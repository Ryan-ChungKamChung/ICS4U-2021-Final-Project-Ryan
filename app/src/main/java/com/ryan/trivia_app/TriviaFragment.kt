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
import com.ryan.trivia_app.databinding.FragmentTriviaBinding
import kotlin.concurrent.thread

/** TriviaFragment class, this is the in-game loop. */
class TriviaFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentTriviaBinding? = null

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
        _binding = FragmentTriviaBinding.inflate(inflater, container, false)

        // Gets the passed data from CategoriesFragment
        val category = requireArguments().getParcelable<Category>("category")
        // Sets the category as the top bar
        binding.txtCategory.text = category!!.name

        thread {
            val json = API().request(
                "https://opentdb.com/api.php?amount=50&category=" + category.id + "&type=multiple"
            )
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
                    var question = questionsArray[questionCount]
                    var lives = 3

                    var answered = false

                    // Show first question
                    showQuestion(binding, questionsArray[questionCount])

                    buttons.forEach { it ->
                        it.setOnClickListener {
                            it as Button

                            if (questionCount == 49) {
                                startActivity(API().internetError(requireActivity()))
                            }
                            if (!answered) {
                                val isCorrect = it.text == question.rightAnswer
                                if (!isCorrect) {
                                    --lives
                                    if (lives == 0) {
                                        startActivity(API().internetError(requireActivity()))
                                    }
                                }
                                showAnswers(binding, it, question.rightAnswer, isCorrect)
                                answered = true
                                question = questionsArray[++questionCount]
                                newQuestion(binding, question, answered)
                                answered = false
                            }
                        }
                    }
                } else {
                    // Goes back to main and shows a Toast
                    startActivity(API().internetError(requireActivity()))
                }
            }
        }
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
                binding.btnAnswer1.setBackgroundColor(Color.parseColor("#F5F5F5"))
                binding.btnAnswer2.setBackgroundColor(Color.parseColor("#F5F5F5"))
                binding.btnAnswer3.setBackgroundColor(Color.parseColor("#F5F5F5"))
                binding.btnAnswer4.setBackgroundColor(Color.parseColor("#F5F5F5"))
                showQuestion(binding, question)
            }, 1000)
        }
    }

    private fun showEndOfGame(binding: FragmentTriviaBinding) {
        binding.endOfGame.visibility = View.VISIBLE
    }
}
