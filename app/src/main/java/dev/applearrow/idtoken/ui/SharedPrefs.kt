package dev.applearrow.idtoken.ui

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment

fun Fragment.saveString(key: String, value: String) {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putString(key, value)
        apply()
    }
}

fun Fragment.retrieveString(key: String): String? {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
    return sharedPref.getString(key, "")
}

fun Application.retrieveOtString(key: String): String? {
    val sharedPref = getSharedPreferences(
        "com.onetrust.otpublishers.headless.preferenceOTT_DEFAULT_USER",
        Context.MODE_PRIVATE
    ) ?: return "not found"
    return sharedPref.getString(key, "")
}
