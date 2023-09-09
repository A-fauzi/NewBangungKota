package com.afauzi.bangungkota.presentation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityMainBinding
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.SmoothBottomBar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigation()

    }

    private fun setUpNavigation() {
        // Temukan NavHostFragment dan dapatkan NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }
}