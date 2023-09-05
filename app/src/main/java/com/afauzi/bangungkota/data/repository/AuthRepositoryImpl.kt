package com.afauzi.bangungkota.data.repository

import androidx.lifecycle.MutableLiveData
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun signInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                val isNewUser = it.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = User(uid = uid, name = name.toString(), email = email.toString())
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value = it.exception?.message.let { excep ->
                    ResponseState.Error(excep.toString())
                }
            }
        }
        return authenticatedUserMutableLiveData
    }
}