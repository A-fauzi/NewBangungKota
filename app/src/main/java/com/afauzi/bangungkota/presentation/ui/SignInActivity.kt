package com.afauzi.bangungkota.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.afauzi.bangungkota.databinding.ActivitySignInBinding
import com.afauzi.bangungkota.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewModel.signInWithGoogle("")

        authViewModel.signOut()

        authViewModel.userLiveData.observe(this) {

        }
    }
}