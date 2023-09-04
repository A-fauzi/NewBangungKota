package com.afauzi.bangungkota.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}