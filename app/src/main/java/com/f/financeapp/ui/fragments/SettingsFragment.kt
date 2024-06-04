package com.f.financeapp.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.f.financeapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}