package com.afauzi.bangungkota.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp


data class Post(
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)
