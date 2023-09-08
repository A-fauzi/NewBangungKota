package com.afauzi.bangungkota.data.repository.post.reply

import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot

class ReplyPostRepositoryImpl: ReplyPostRepository {
    private val databaseReference = FirebaseDatabase.getInstance().reference

    override fun createReplyPost(data: Post.ReplyPost, postId: String, childParent: String): Task<Void> {
        val postCommentRef = databaseReference.child(childParent).child(postId).child(data.id.toString())
        return postCommentRef.setValue(data)
    }

    override suspend fun getReplyPost(childParent: String, postId: String, snapshot: (DataSnapshot) -> Unit, error: (DatabaseError) -> Unit): ValueEventListener {
        val postCommentRef = databaseReference.child(childParent).child(postId)
        return postCommentRef.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                error(error)
            }

        })
    }
}