package dev.applearrow.poctemplate.ui.main

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.applearrow.poctemplate.R

class ConfigFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_config, rootKey)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        activity?.let {
            val fragmentManager = it.supportFragmentManager
            fragmentManager.fragmentFactory.instantiate(
                it.classLoader,
                pref.fragment
            ).apply {
                arguments = args
                setTargetFragment(caller, 0)
            }
        }

        val key = resources.getString(R.string.email_address_pref)
        val editTextPreference = findPreference<EditTextPreference>(key)
        editTextPreference?.let { editTextPref ->
            editTextPref.setOnBindEditTextListener {
                it.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
        }

        return true
    }
}

