package com.afauzi.bangungkota.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Post(
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
): Parcelable {
    data class ReplyPost(
        val id: String? = null,
        val postId: String? = null,
        val userId: String? = null,
        val text: String? = null,
        val created_at: String? = null
    )
}
