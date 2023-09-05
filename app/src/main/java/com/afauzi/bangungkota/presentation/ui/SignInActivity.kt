package com.afauzi.bangungkota.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivitySignInBinding
import com.afauzi.bangungkota.domain.state.ResponseState
import com.afauzi.bangungkota.presentation.ui.main.MainActivity
import com.afauzi.bangungkota.presentation.viewmodels.AuthViewModel
import com.afauzi.bangungkota.utils.Constant
import com.afauzi.bangungkota.utils.Constant.LOG_TAG
import com.afauzi.bangungkota.utils.Constant.RC_SIGN_IN
import com.afauzi.bangungkota.utils.CustomViews
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.cuneytayyildiz.onboarder.utils.visible
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleSignInClient()
        binding.signInButton.setOnClickListener {
            signInUsingGoogle()
        }
    }


    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInUsingGoogle() {
        val signInGoogleIntent = googleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                getGoogleAuthCredential(account)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                toast(this, e.message)
            }
        }
    }

    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokeId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokeId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential)
        authViewModel.authenticateUserLiveData.observe(this) { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    authenticatedUser.message?.let {
                        toast(this, it)
                    }
                }
                is ResponseState.Success -> {
                    if (authenticatedUser.data != null){
                        //update ui
                        toast(this, "Hi ${authenticatedUser.data.name}")
                    } else {
                        toast(this, "data null")
                    }
                }
                is ResponseState.Loading -> {
                    //show progress
                    binding.progressbar.visibility = View.VISIBLE
                }
            }
        }
    }

}