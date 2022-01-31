/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Fragment that is attached to MainActivity when btnLeaderboard is clicked in MenuFragment.
 * Displays the local leaderboard.
 */

package com.ryan.trivia_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ryan.trivia_app.R
import com.ryan.trivia_app.controller.Transfer
import com.ryan.trivia_app.databinding.FragmentLeaderboardBinding
import org.json.JSONArray
import org.json.JSONException

class LeaderboardFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentLeaderboardBinding? = null

    /** Binding getter. */
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Binds leaderboard to View
        bindLeaderboard()

        // Transfers back to MenuFragment
        binding.btnBack.setOnClickListener {
            Transfer().transferToFragment(
                parentFragmentManager, R.id.fragmentPlaceholder, MenuFragment()
            )
        }
    }

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
        // Inflate the layout for this fragment
        _binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }

    /** Binds leaderboard stored in persistent storage to the View. */
    private fun bindLeaderboard() {
        // Leaderboard in persistent storage
        val prefLeaderboard = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val jsonLeaderboard = try {
            JSONArray(
                prefLeaderboard.getString(
                    "jsonLeaderboard", "empty"
                )
            )
        } catch (e: JSONException) {
            null
        }
        // Leaderboard of nulls
        val leaderboard = arrayOfNulls<String>(10)
        // Populate leaderboard with leaderboard in persistent memory
        if (jsonLeaderboard != null) {
            for (iterator in leaderboard.indices) {
                try {
                    leaderboard[iterator] = jsonLeaderboard.getString(iterator)
                } catch (e: JSONException) {
                    leaderboard[iterator] = null
                }
            }

            // Update text
            binding.txtLeaderboard.text = getString(
                R.string.leaderboard_text,
                leaderboard[0], leaderboard[1], leaderboard[2],
                leaderboard[3], leaderboard[4], leaderboard[5],
                leaderboard[6], leaderboard[7], leaderboard[8], leaderboard[9]
            )
        } else {
            "Play a game to start off the leaderboard!".also { binding.txtLeaderboard.text = it }
        }
    }
}
