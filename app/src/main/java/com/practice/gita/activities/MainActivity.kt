package com.practice.gita.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practice.gita.R


class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Gita)
        setContentView(R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment_container)
        navView = findViewById(R.id.bottom_nav_view)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.verseFragment || destination.id == R.id.shareFragment)
                navView.visibility = View.GONE
            else navView.visibility = View.VISIBLE

        }

    }
}