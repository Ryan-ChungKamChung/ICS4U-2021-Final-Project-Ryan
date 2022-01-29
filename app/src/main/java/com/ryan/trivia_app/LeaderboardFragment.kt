package com.ryan.trivia_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ryan.trivia_app.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentLeaderboardBinding? = null
    /** Binding getter. */
    private val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }
}