package com.afauzi.bangungkota.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Event(

    val id: String? = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    var image: String = "",
    val date: String = "",
    val time: String = "",
    val peopleJoin: @RawValue List<User>? = arrayListOf(),
    val createdBy: String = "",


    val createdAt: Timestamp = Timestamp.now(),

    val updatedAt: Timestamp = Timestamp.now(),

    ): Parcelable