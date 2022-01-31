/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Fragment that is attached to GameActivity pre-game, where the user gets to choose a category
 * inside the trivia API.
 */

package com.ryan.trivia_app.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ryan.trivia_app.controller.CategoriesController
import com.ryan.trivia_app.controller.Transfer
import com.ryan.trivia_app.databinding.FragmentCategoriesBinding
import com.ryan.trivia_app.model.CategoryAPIRequest

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

        // Array of buttons
        val buttons = arrayOf(
            binding.btnChoice1, binding.btnChoice2,
            binding.btnChoice3, binding.btnChoice4
        )
        // Array of categories
        val categories = CategoryAPIRequest("https://opentdb.com/api_category.php")
            .execute()
            .get()
        // Controller
        val categoriesController = CategoriesController(parentFragmentManager, buttons, categories)

        if (categories != null) {
            // Binds text to buttons
            categoriesController.bindToView()
            categoriesController.setOnClickListeners()
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
