package com.afauzi.bangungkota.data.remote.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealtimeDB(private val parentChild: String) {
    private val reference = FirebaseDatabase.getInstance().reference
    // Specify the database reference where you want to store the data

    fun create(data: Any) {
        reference.setValue(data)
    }
}