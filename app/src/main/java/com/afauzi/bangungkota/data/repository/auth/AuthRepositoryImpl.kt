package com.afauzi.bangungkota.data.repository.auth

import androidx.lifecycle.MutableLiveData
import com.afauzi.bangungkota.domain.model.User
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryImpl: AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    val photo = firebaseUser.photoUrl
                    val phoneNumber = firebaseUser.phoneNumber
                    val user = User(uid = uid, name = name.toString(), email = email.toString(), photo = photo.toString(), phoneNumber = phoneNumber.toString())
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

    override fun checkCurrentUser(): Boolean {
        return firebaseAuth.currentUser !== null
    }
}