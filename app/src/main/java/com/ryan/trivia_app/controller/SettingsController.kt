package com.ryan.trivia_app.controller

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentManager
import com.ryan.trivia_app.R
import com.ryan.trivia_app.databinding.FragmentSettingsBinding
import com.ryan.trivia_app.view.MenuFragment

class SettingsController(context: Context, private val binding: FragmentSettingsBinding) {

    private val settings = PreferenceManager.getDefaultSharedPreferences(context)
    private var fx = settings.getBoolean("fx", true)

    fun bindToFXButton() {
        binding.btnFX.text = if (fx) "ON" else "OFF"
        binding.btnFX.setBackgroundResource(
            if (fx) R.drawable.fx_button_green else R.drawable.fx_button_red
        )
    }

    fun setOnBtnFXClickListener() =
        binding.btnFX.setOnClickListener {
            // fx becomes the opposite of its initial state. Ex: True -> False
            fx = !fx
            // Reflect the changes on the button
            bindToFXButton()

            // Updates the persistent storage to remember the user's choice
            val edit = settings.edit()
            edit.putBoolean("fx", fx)
            edit.apply()
        }

    fun openBrowserOnClick(activity: Activity, url: String) =
        binding.btnLicense.setOnClickListener { Transfer().transferToBrowser(activity, url) }

    fun onBackPressListener(fragmentManager: FragmentManager) {
        binding.btnBack.setOnClickListener {
            Transfer().transferToFragment(
                fragmentManager, R.id.fragmentPlaceholder, MenuFragment()
            )
        }
    }
}
