package com.ryan.trivia_app

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                val questionsArray = API().parseQuestions(json)
                // UI thread
                requireActivity().runOnUiThread {
                    // Array of buttons
                    bindToView(binding, questionsArray[0])
                }
            } else {
                // Goes back to main and shows a Toast
                startActivity(API().internetError(requireActivity()))
                (context as Activity).overridePendingTransition(0, 0)
            }
        }
        return binding.root
    }

    private fun bindToView(binding: FragmentTriviaBinding, question: Question) {
        binding.txtQuestion.text = question.question
        binding.txtRightAnswer.text = question.rightAnswer
        binding.txtIncorrectAnswer1.text = question.wrongAnswer1
        binding.txtIncorrectAnswer2.text = question.wrongAnswer2
        binding.txtIncorrectAnswer3.text = question.wrongAnswer3
    }
}
