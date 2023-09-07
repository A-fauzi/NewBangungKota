package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afauzi.bangungkota.data.repository.user.UserRepository
import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    private val _userCreateEvent = MutableLiveData<Int>()
    val userCreateEvent: LiveData<Int> get() = _userCreateEvent
    fun sizeCreateEvent(size: Int) {
        _userCreateEvent.value = size
    }

    fun saveData(documentId: String, data: User): Task<Void> {
        return userRepository.saveUser(documentId, data)
    }

    suspend fun getUserById(documentId: String): Task<DocumentSnapshot> {
        return userRepository.getUserById(documentId)
    }
}