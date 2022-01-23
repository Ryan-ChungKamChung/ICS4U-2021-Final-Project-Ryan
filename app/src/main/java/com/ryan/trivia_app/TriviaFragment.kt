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
                    var question = 0
                    var transferred = false
                    bindToView(binding, questionsArray[question])
                    binding.btnAnswer1.setOnClickListener {
                        if (question == 49) {
                            transferred = true
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!newQuestion(binding, it as Button, questionsArray[question++])
                            && !transferred
                        ) {
                            if (lives > 0) {
                                lives--
                            } else {
                                API().internetError(requireActivity())
                            }
                        }
                    }
                    binding.btnAnswer2.setOnClickListener {
                        if (question == 49) {
                            transferred = true
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!newQuestion(binding, it as Button, questionsArray[question++])
                            && !transferred
                        ) {
                            if (lives > 0) {
                                lives--
                            } else {
                                API().internetError(requireActivity())
                            }
                        }
                    }
                    binding.btnAnswer3.setOnClickListener {
                        if (question == 49) {
                            transferred = true
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!newQuestion(binding, it as Button, questionsArray[question++])
                            && !transferred
                        ) {
                            if (lives > 0) {
                                lives--
                            } else {
                                API().internetError(requireActivity())
                            }
                        }
                    }
                    binding.btnAnswer4.setOnClickListener {
                        if (question == 49) {
                            transferred = true
                            startActivity(API().internetError(requireActivity()))
                        }
                        if (!newQuestion(binding, it as Button, questionsArray[question++])
                            && !transferred
                        ) {
                            if (lives > 0) {
                                lives--
                            } else {
                                API().internetError(requireActivity())
                            }
                        }
                    }
                }
            } else {
                // Goes back to main and shows a Toast
                startActivity(API().internetError(requireActivity()))
                (context as Activity).overridePendingTransition(0, 0)
            }
        }
        return binding.root
    }

//    fun onButtonPress(view: View) {
//        when (view) {
//            binding.btnAnswer1 ->
//            binding.btnAnswer2 ->
//            binding.btnAnswer3 ->
//            binding.btnAnswer4 ->
//        }
//    }

    /**
     * Binds question-relevant text to the View.
     *
     * @param binding IDs of all Views
     * @param question information about the question
     */
    private fun bindToView(binding: FragmentTriviaBinding, question: Question) {
        val answers = arrayOf(
            question.rightAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        )
        answers.shuffle()
        binding.txtQuestion.text = question.question
        binding.btnAnswer1.text = answers[0] as String
        binding.btnAnswer2.text = answers[1] as String
        binding.btnAnswer3.text = answers[2] as String
        binding.btnAnswer4.text = answers[3] as String
    }

    private fun chosenAnswer(
        binding: FragmentTriviaBinding,
        button: Button,
        question: Question
    ): Boolean {
        val buttons = arrayOf(
            binding.btnAnswer1, binding.btnAnswer2, binding.btnAnswer3, binding.btnAnswer4
        )
        for (buttonElement: Button in buttons) {
            if (buttonElement.text == question.rightAnswer) {
                buttonElement.setBackgroundColor(Color.parseColor("#33B16F"))
            }
        }
        if (!isCorrect(button, question)) {
            button.setBackgroundColor(Color.parseColor("#D84761"))
        }
        return isCorrect(button, question)
    }

    private fun isCorrect(button: Button, question: Question): Boolean =
        button.text == question.rightAnswer

    private fun newQuestion(
        binding: FragmentTriviaBinding,
        button: Button,
        question: Question
    ): Boolean {
        val userCorrect = chosenAnswer(binding, button, question)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnAnswer1.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer2.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer3.setBackgroundColor(Color.parseColor("#F5F5F5"))
            binding.btnAnswer4.setBackgroundColor(Color.parseColor("#F5F5F5"))
            bindToView(binding, question)
        }, 1000)
        return userCorrect
    }
}
