package dev.applearrow.idtoken.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import dev.applearrow.idtoken.R


class MainViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        const val TAG = "MainVm"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
    val intMsg = MutableLiveData(R.string.app_name)
    val intError = MutableLiveData(0)

    fun hideError() {
        intError.value = 0
    }

    init {
        intMsg.value = R.string.app_name
    }
}

