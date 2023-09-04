package com.afauzi.bangungkota.domain.usecase

import com.afauzi.bangungkota.data.repository.AuthRepository
import com.afauzi.bangungkota.domain.model.User
import com.google.firebase.auth.FirebaseUser

class SignInWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend fun execute(idToken: String): FirebaseUser? {
        val result = authRepository.signInWithGoogle(idToken)
        return authRepository.getCurrentUser()
    }
}