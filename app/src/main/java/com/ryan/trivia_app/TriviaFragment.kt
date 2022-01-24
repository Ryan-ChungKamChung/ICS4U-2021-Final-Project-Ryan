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
            if (json != null) {
                // UI thread
                requireActivity().runOnUiThread {
                    val questionsArray = API().parseQuestions(json)

                    var lives = 3
                    var questionCount = 0
                    var question: Question
                    var answered = false

                    showQuestion(binding, questionsArray[questionCount])
                    binding.btnAnswer1.setOnClickListener { it as Button
                        if (questionCount == 49 || lives <= 0) {
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!answered) {
                            question = questionsArray[++questionCount]

                            val isCorrect = isCorrect(it, question)
                            if (!isCorrect) { lives-- }

                            showAnswers(binding, it, isCorrect, question)
                            answered = true
                            newQuestion(binding, question)
                        }
                    }
                    binding.btnAnswer2.setOnClickListener { it as Button
                        if (questionCount == 49 || lives <= 0) {
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!answered) {
                            question = questionsArray[++questionCount]

                            val isCorrect = isCorrect(it, question)
                            if (!isCorrect) { lives-- }

                            showAnswers(binding, it, isCorrect, question)
                            answered = true
                            newQuestion(binding, question)
                        }
                    }
                    binding.btnAnswer3.setOnClickListener { it as Button
                        if (questionCount == 49 || lives <= 0) {
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!answered) {
                            question = questionsArray[++questionCount]

                            val isCorrect = isCorrect(it, question)
                            if (!isCorrect) { lives-- }

                            showAnswers(binding, it, isCorrect, question)
                            answered = true
                            newQuestion(binding, question)
                        }
                    }
                    binding.btnAnswer4.setOnClickListener { it as Button
                        if (questionCount == 49 || lives <= 0) {
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!answered) {
                            question = questionsArray[++questionCount]

                            val isCorrect = isCorrect(it, question)
                            if (!isCorrect) { lives-- }

                            showAnswers(binding, it, isCorrect, question)
                            answered = true
                            newQuestion(binding, question)
                        }
                    }
                }
            } else {
                // Goes back to main and shows a Toast
                startActivity(API().internetError(requireActivity()))
            }
        }
        return binding.root
    }

    /**
     * Binds question-relevant text to the View.
     *
     * @param binding IDs of all Views
     * @param question information about the question
     */
    private fun showQuestion(binding: FragmentTriviaBinding, question: Question) {
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        val answers = arrayOf(
            question.rightAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        )
        answers.shuffle()
        binding.txtQuestion.text = question.question
        for (iterator in buttons.indices) { buttons[iterator].text = answers[iterator] }
    }

    /**
     * Shows the right answer and if the user was wrong.
     *
     * @param binding binding of the fragment
     * @param button button that the user pressed
     * @param question current question
     */
    private fun showAnswers(
        binding: FragmentTriviaBinding,
        button: Button,
        rightAnswer: Boolean,
        question: Question) {
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        buttons.forEach { buttonElement ->
            if (buttonElement.text == question.rightAnswer) {
                buttonElement.setBackgroundColor(Color.parseColor("#33B16F"))
            }
        }
        if (!rightAnswer) {
            button.setBackgroundColor(Color.parseColor("#D84761"))
        }
    }

    private fun isCorrect(button: Button, question: Question): Boolean =
        button.text == question.rightAnswer

    private fun newQuestion(
        binding: FragmentTriviaBinding,
        question: Question
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnAnswer1.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer2.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer3.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer4.setBackgroundColor(Color.parseColor("#F5F5F5"))
            showQuestion(binding, question)
        }, 1000)
    }
}
