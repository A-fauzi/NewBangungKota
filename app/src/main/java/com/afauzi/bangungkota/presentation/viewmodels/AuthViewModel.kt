package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.usecase.SignInWithGoogleUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val signInWithGoogleUseCase: SignInWithGoogleUseCase): ViewModel() {
    private val _userLiveData = MutableLiveData<FirebaseUser?>()
    val userLiveData: LiveData<FirebaseUser?> = _userLiveData

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val user = signInWithGoogleUseCase.execute(idToken)
            _userLiveData.postValue(user)
        }
    }

//    fun getCurrentUser(): FirebaseUser? {
//        return signInWithGoogleUseCase.getCurrentUser()
//    }

    fun signOut() {
        // Panggil metode signOut di lapisan data jika diperlukan
    }
}