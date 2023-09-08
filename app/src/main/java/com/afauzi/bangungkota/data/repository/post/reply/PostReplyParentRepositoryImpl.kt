package com.afauzi.bangungkota.data.repository.post.reply

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class PostReplyParentRepositoryImpl: PostReplyParentRepository<Post.ReplyPost> {
    private val db = FirebaseFirestore.getInstance()
    override fun replyPostPagingSource(): Flow<PagingData<Post.ReplyPost>> {
        TODO("Not yet implemented")
    }

    override fun createPost(data: Post.ReplyPost, documentId: String): Task<Void> {
        return db.collection("reply_post_parent")
            .document(documentId)
            .set(data)
    }

    override fun deletePost(documentId: String): Task<Void> {
        TODO("Not yet implemented")
    }
}