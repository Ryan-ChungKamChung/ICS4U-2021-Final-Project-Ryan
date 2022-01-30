package com.ryan.trivia_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ryan.trivia_app.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    /** Binding to access XML components. */
    private var _binding: FragmentSettingsBinding? = null

    /** Binding getter. */
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var fx = settings.getBoolean("fx", true)
        bindToFXButton(binding, fx)

        binding.btnFX.setOnClickListener {
            fx = !fx
            bindToFXButton(binding, fx)

            val edit = settings.edit()
            edit.putBoolean("fx", fx)
            edit.apply()
        }

        binding.btnAPI.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://opentdb.com/")))
        }

        binding.btnLicense.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://creativecommons.org/licenses/by-sa/4.0/"
                    )
                )
            )
        }
    }

    private fun bindToFXButton(binding: FragmentSettingsBinding, fx:Boolean) {
        binding.btnFX.text = if (fx) "ON" else "OFF"
        binding.btnFX.setBackgroundResource(
            if (fx) R.drawable.fx_button_green else R.drawable.fx_button_red
        )
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
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }
}
