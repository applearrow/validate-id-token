package dev.applearrow.idtoken.ui

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

fun Fragment.saveString(key: String, value: String) {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
    with(sharedPref.edit()) {
        putString(key, value)
        apply()
    }
}

fun Fragment.retrieveString(key: String): String? {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
    return sharedPref.getString(key, "")
}

fun Application.retrieveOtString(key: String): String? {
    val sharedPref = getSharedPreferences(
        "com.onetrust.otpublishers.headless.preferenceOTT_DEFAULT_USER",
        Context.MODE_PRIVATE
    ) ?: return "not found"
    return sharedPref.getString(key, "")
}
