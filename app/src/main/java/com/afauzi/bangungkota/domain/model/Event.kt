package com.afauzi.bangungkota.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val image: String = "",
    val date: String = "",
    val time: String = "",
    val peopleJoin: List<User> = arrayListOf(),
    val createdBy: String = "",


    val createdAt: Timestamp = Timestamp.now(),

    val updatedAt: Timestamp = Timestamp.now(),
)
