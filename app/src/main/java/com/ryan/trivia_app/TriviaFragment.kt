package com.ryan.trivia_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ryan.trivia_app.databinding.FragmentTriviaBinding

/**
 * A simple [Fragment] subclass.
 */
class TriviaFragment : Fragment() {
    private var _binding: FragmentTriviaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTriviaBinding.inflate(inflater, container, false)

        val value = requireArguments().getString("category")

        binding.txtCategory.text = value

        return binding.root
    }
}