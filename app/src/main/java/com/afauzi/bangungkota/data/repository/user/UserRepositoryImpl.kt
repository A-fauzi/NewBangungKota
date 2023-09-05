package com.afauzi.bangungkota.data.repository.user

import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl: UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun saveUser(documentId: String, data: User): Task<Void> {
        return db.collection("users")
            .document(documentId)
            .set(data)
    }
}