package com.ryan.trivia_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.databinding.FragmentTriviaBinding
import kotlin.concurrent.thread
import org.json.JSONObject

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

        val questionsArray = ArrayList<Question>()
        // Gets the passed data from CategoriesFragment
        val category = requireArguments().getParcelable<Category>("category")
        // Sets the category as the top bar
        binding.txtCategory.text = category!!.name
        thread {
            val json = API().request(
                "https://opentdb.com/api.php?amount=50?category=" + category.id
            )
            if (json != null) {
                val results = JSONObject(json).getJSONArray("results")
                for (iterator in 0 until results.length()) {
                    val incorrectAnswers = ArrayList<String>()
                    val jsonIncorrectAnswers = (results[iterator] as JSONObject).getJSONArray("incorrect_answers")
                    for (iterator2 in 0 until jsonIncorrectAnswers.length()) {
                        incorrectAnswers.add((jsonIncorrectAnswers[iterator2].toString()))
                    }
                    questionsArray.add(
                        Question(
                            (results[iterator] as JSONObject).getString("question"),
                            (results[iterator] as JSONObject).getString("correct_answer"),
                            incorrectAnswers[0],
                            incorrectAnswers[1],
                            incorrectAnswers[2]
                        )
                    )
                }
            }
        }
        return binding.root
    }
}
