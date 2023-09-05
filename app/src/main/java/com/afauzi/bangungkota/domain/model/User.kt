package com.afauzi.bangungkota.domain.model

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photo: String? = "",

    @PropertyName("phone_number")
    val phoneNumber: String = "",

    @PropertyName("is_authenticated")
    var isAuthenticated: Boolean = false,

    @PropertyName("is_new")
    var isNew: Boolean? = false,

    @PropertyName("is_created")
    var isCreated: Boolean = false,

    @PropertyName("create_at")
    val createAt: Timestamp? = Timestamp.now(),

    @PropertyName("update_at")
    val updateAt: Timestamp? = Timestamp.now(),
)
