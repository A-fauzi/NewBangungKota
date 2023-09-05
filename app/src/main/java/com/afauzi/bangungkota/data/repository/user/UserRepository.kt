package com.afauzi.bangungkota.data.repository.user

import com.afauzi.bangungkota.domain.model.User
import com.google.android.gms.tasks.Task

interface UserRepository {
    fun saveUser(documentId: String, data: User): Task<Void>
}