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

    private val _user = MutableLiveData<User>()
    val userLiveData: LiveData<User> get() = _user

    suspend fun getUserByIdLiveData(userId: String) {
        userRepository.getUserById(userId)
            .addOnSuccessListener {
                if (it.exists()) {

                    val data = User(
                        uid = it.getString("uid").toString(),
                        name = it.getString("name").toString(),
                        email = it.getString("email").toString(),
                        photo = it.getString("photo").toString(),
                    )
                    _user.value = data
                } else {
                    // data not exists
                }
            }
            .addOnFailureListener {
                // on failure get user
            }
    }

    fun saveData(documentId: String, data: User): Task<Void> {
        return userRepository.saveUser(documentId, data)
    }

    // Nanti ini hapus, karna sudah digantikan livedata
    suspend fun getUserById(documentId: String): Task<DocumentSnapshot> {
        return userRepository.getUserById(documentId)
    }
}