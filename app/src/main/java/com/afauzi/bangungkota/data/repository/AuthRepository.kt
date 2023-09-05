package com.afauzi.bangungkota.data.repository

import androidx.lifecycle.MutableLiveData
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    fun signInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<ResponseState<User>>
}