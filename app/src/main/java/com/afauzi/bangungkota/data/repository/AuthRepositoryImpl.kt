package com.afauzi.bangungkota.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {
    private val auth = Firebase.auth
    override suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun signOut() {
        auth.signOut()
    }
}