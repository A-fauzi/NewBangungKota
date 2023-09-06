package com.afauzi.bangungkota.data.repository.user

import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface UserRepository {
    fun saveUser(documentId: String, data: User): Task<Void>
    fun getUserById(documentId: String): Task<DocumentSnapshot>
}