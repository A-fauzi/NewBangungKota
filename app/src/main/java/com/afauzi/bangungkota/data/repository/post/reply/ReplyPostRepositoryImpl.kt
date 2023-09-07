package com.afauzi.bangungkota.data.repository.post.reply

import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot

class ReplyPostRepositoryImpl: ReplyPostRepository {

    override fun createReplyPost(data: Post.ReplyPost, postId: String): Task<Void> {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val postCommentRef = databaseReference.child("comments").child(postId).child(data.id.toString())
        return postCommentRef.setValue(data)
    }

    override suspend fun getReplyPost(): Task<DocumentSnapshot> {
        TODO("Not yet implemented")
    }
}