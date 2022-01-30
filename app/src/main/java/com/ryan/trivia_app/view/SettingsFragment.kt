/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Fragment that is attached to MainActivity when btnSettings is clicked in MenuFragment.
 * Displays settings and credits.
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
import com.ryan.trivia_app.databinding.FragmentSettingsBinding

/** SettingsFragment class, where the settings and credits are located inside MainActivity. */
class SettingsFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentSettingsBinding? = null

    /** Binding getter. */
    private val binding get() = _binding!!

    /**
     * After the view is created with binding, create onClickListeners.
     *
     * @param view the view with the inflated layout.
     * @param savedInstanceState Bundle holding instanceState.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FX setting in persistent storage
        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var fx = settings.getBoolean("fx", true)
        bindToFXButton(binding, fx)

        // FX toggle button
        binding.btnFX.setOnClickListener {
            // fx becomes the opposite of its initial state. Ex: True -> False
            fx = !fx
            // Reflect the changes on the button
            bindToFXButton(binding, fx)

            // Updates the persistent storage to remember the user's choice
            val edit = settings.edit()
            edit.putBoolean("fx", fx)
            edit.apply()
        }

        // Opens up the user's default browser, displays the API used for this app
        binding.btnAPI.setOnClickListener {
            Transfer().transferToBrowser(requireActivity(), "https://opentdb.com/")
        }

        // Opens up the user's default browser, used API's license
        binding.btnLicense.setOnClickListener {
            Transfer().transferToBrowser(
                requireActivity(), "https://creativecommons.org/licenses/by-sa/4.0/"
            )
        }

        // Transfers back to MenuFragment
        binding.btnBack.setOnClickListener {
            Transfer().transferToFragment(
                parentFragmentManager, R.id.fragmentPlaceholder, MenuFragment()
            )
        }
    }

    private fun bindToFXButton(binding: FragmentSettingsBinding, fx: Boolean) {
        binding.btnFX.text = if (fx) "ON" else "OFF"
        binding.btnFX.setBackgroundResource(
            if (fx) R.drawable.fx_button_green else R.drawable.fx_button_red
        )
    }

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
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }
}
