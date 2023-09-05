package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afauzi.bangungkota.data.repository.auth.AuthRepository
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {
    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = authRepository.signInWithGoogle(googleAuthCredential)
    }

    private val _isUserSignedIn = MutableLiveData<Boolean>()

    val isUserSignedIn: LiveData<Boolean> = _isUserSignedIn
    fun currentUser() {
        _isUserSignedIn.value = authRepository.checkCurrentUser()
    }
}