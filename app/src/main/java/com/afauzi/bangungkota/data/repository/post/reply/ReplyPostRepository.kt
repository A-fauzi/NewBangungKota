package com.afauzi.bangungkota.data.repository.post.reply

import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface ReplyPostRepository {
    fun createReplyPost(data: Post.ReplyPost, postId: String): Task<Void>
    suspend fun getReplyPost(): Task<DocumentSnapshot>
}