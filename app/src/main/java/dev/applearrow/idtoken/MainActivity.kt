package dev.applearrow.idtoken

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Pass intent extras as parameters to the start destination
        navController = navHostFragment.navController
        if (intent.extras != null && intent.extras?.containsKey(getString(R.string.jwt_token_pref)) == true) {
            navController.setGraph(navController.graph, intent.extras)
            intent.extras?.keySet()?.forEach { key ->
                Log.d(TAG, "EXTRAS: $key = ${intent.extras?.get(key)}")
            }
            // Clear the intent extras to avoid rotation issues
            intent.replaceExtras(Bundle())
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.configFragment -> showBackButton()
                else -> {
                    hideBackButton()
                }
            }
        }
    }

    private fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun hideBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    companion object {
        const val TAG = "Main"
    }
}