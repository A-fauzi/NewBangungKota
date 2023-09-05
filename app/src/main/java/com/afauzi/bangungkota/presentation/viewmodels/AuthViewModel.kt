package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afauzi.bangungkota.data.repository.AuthRepository
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.state.ResponseState
import com.afauzi.bangungkota.domain.usecase.SignInWithGoogleUseCase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {
    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = authRepository.signInWithGoogle(googleAuthCredential)
    }
}