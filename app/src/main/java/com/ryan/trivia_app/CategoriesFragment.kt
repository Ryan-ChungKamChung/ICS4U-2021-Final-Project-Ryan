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
import com.ryan.trivia_app.databinding.FragmentCategoriesBinding
import java.net.URL
import kotlin.concurrent.thread
import kotlinx.serialization.*
import org.json.JSONObject
import kotlin.random.Random

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

        val allCategories = ArrayList<Category>()
        val usedCategories = ArrayList<Category>()
        val allChoiceButtons = arrayOf(
            binding.btnChoice1, binding.btnChoice2, binding.btnChoice3, binding.btnChoice4
        )

        thread {
            val json = try {
                URL("https://opentdb.com/api_category.php").readText()
            } catch (e: Exception) {
                return@thread
            }

            val jsonArray = JSONObject(json).getJSONArray("trivia_categories")
            for (iterator in 0 until jsonArray.length()) {
                val rawName = (jsonArray[iterator] as JSONObject).getString("name")
                val name = if (rawName.startsWith("Entertainment:")) {
                    rawName.drop(15)
                } else {
                    rawName
                }
                allCategories.add(
                    Category(
                        (jsonArray[iterator] as JSONObject).getInt("id"),
                        name
                    )
                )
            }

            requireActivity().runOnUiThread {
                for (iterator in 0 until allChoiceButtons.count()) {
                    val randomNumber = Random.nextInt(0, allCategories.count())
                    allChoiceButtons[iterator].text = allCategories[randomNumber].name
                    usedCategories.add(allCategories[randomNumber])
                }
            }
        }

        /* onClickListeners for the user's choice of category.
           Starts the transfer process to the start of the game */
        binding.btnChoice1.setOnClickListener {
            transferToQuizFragment(binding.btnChoice1, usedCategories[0])
        }
        binding.btnChoice2.setOnClickListener {
            transferToQuizFragment(binding.btnChoice2, usedCategories[1])
        }
        binding.btnChoice3.setOnClickListener {
            transferToQuizFragment(binding.btnChoice3, usedCategories[2])
        }
        binding.btnChoice4.setOnClickListener {
            transferToQuizFragment(binding.btnChoice4, usedCategories[3])
        }

        return binding.root
    }

    /**
     * Initiates a transition and replaces the fragment by TriviaFragment.
     *
     * @param button the button that was clicked by the user.
     */
    private fun transferToQuizFragment(button: Button, category: Category) {
        // Sets chosen button to green
        button.setBackgroundColor(Color.parseColor("#33B16F"))

        // Executes this code 1 second after the button was set to green
        Handler(Looper.getMainLooper()).postDelayed({
            // Adds the chosen category to the bundle to be sent to TriviaFragment
            val args = Bundle()
            args.putParcelable("category", category)
            val triviaFragment = TriviaFragment()
            triviaFragment.arguments = args

            // Replaces this fragment with TriviaFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, triviaFragment)
                .commit()
        }, 1000)
    }
}
