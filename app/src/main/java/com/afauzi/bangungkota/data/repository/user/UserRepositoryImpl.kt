package com.afauzi.bangungkota.data.repository.user

import com.afauzi.bangungkota.data.remote.firebase.FireStoreManager
import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl: UserRepository {

    private val fireStoreManager: FireStoreManager<User> = FireStoreManager("users")

    override fun saveUser(documentId: String, data: User): Task<Void> {
        return fireStoreManager.create(data, documentId)
    }

    override suspend fun getUserById(documentId: String): Task<DocumentSnapshot> {
        return fireStoreManager.getData(documentId)
    }
}