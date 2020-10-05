package com.app.timer

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import timer.app.com.R

class SettingsActivityFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}