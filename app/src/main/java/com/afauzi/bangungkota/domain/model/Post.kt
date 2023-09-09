package com.afauzi.bangungkota.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.afauzi.bangungkota.utils.UtilityLibrary
import com.afauzi.bangungkota.utils.UtilityLibrary.timeStamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.sql.Time
import java.util.Date

@Parcelize
data class Post(
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
    val created_at: String? = timeStamp(),
    val updated_at: String? = timeStamp()
): Parcelable {
    data class ReplyPost(
        val id: String? = null,
        val postId: String? = null,
        val userId: String? = null,
        val text: String? = null,
        val created_at: String? = timeStamp()
    )
}
