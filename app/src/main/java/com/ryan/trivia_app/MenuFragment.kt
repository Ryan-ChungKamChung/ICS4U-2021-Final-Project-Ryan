package com.ryan.trivia_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ryan.trivia_app.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentMenuBinding? = null
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
        _binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // onClickListener to start the game
        binding.btnStart.setOnClickListener {
            startActivity(Intent(activity, GameActivity::class.java))
        }

        binding.btnLeaderboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, LeaderboardFragment())
                .commit()
        }

        binding.btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, SettingsFragment())
                .commit()
        }
    }
}