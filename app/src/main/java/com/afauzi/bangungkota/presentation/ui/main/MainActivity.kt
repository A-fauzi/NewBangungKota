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
import com.afauzi.bangungkota.presentation.ui.main.fragment.AccountFragment
import com.afauzi.bangungkota.presentation.ui.main.fragment.CommunityFragment
import com.afauzi.bangungkota.presentation.ui.main.fragment.HomeFragment
import com.afauzi.bangungkota.presentation.ui.main.fragment.MapsFragment
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint

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
//        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setItemSelected(R.id.homeFragmentMenu)
        binding.bottomNav.setOnItemSelectedListener {
            when(it) {
                R.id.homeFragmentMenu -> {
                    openMainFragment()
                }
                R.id.mapsFragmentMenu -> {
                    val mapFragment = MapsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mapFragment).commit()

                }
                R.id.communityFragmentMenu -> {
                    val communityFragment = CommunityFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, communityFragment).commit()

                }
                R.id.accountFragmentMenu -> {
                    val accFragment = AccountFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, accFragment).commit()

                }
            }
        }
    }

    private fun openMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, HomeFragment())
        transaction.commit()
    }
}