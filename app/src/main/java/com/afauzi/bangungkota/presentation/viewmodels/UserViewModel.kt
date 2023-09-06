package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.afauzi.bangungkota.data.repository.user.UserRepository
import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    fun saveData(documentId: String, data: User): Task<Void> {
        return userRepository.saveUser(documentId, data)
    }

    suspend fun getUserById(documentId: String): Task<DocumentSnapshot> {
        return userRepository.getUserById(documentId)
    }
}