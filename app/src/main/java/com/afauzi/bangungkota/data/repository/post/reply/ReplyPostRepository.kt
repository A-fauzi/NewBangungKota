package com.afauzi.bangungkota.data.repository.post.reply

import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot

interface ReplyPostRepository {
    fun createReplyPost(data: Post.ReplyPost, postId: String, childParent: String): Task<Void>
    suspend fun getReplyPost(childParent: String, postId: String, snapshot: (DataSnapshot) -> Unit, error: (DatabaseError) -> Unit): ValueEventListener
}