package com.afauzi.bangungkota.data.repository.post.reply

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.data.repository.post.PostPagingSource
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class PostReplyParentRepositoryImpl: PostReplyParentRepository<Post.ReplyPost> {
    private val db = FirebaseFirestore.getInstance()

    override fun createPost(collection: String, data: Post.ReplyPost, documentId: String): Task<Void> {
        return db.collection(collection)
            .document(documentId)
            .set(data)
    }

    override fun deletePost(documentId: String): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun replyPostPagingSource(postId: String): Flow<PagingData<Post.ReplyPost>> {
        val pagingConfig = PagingConfig(
            pageSize = 10,
            prefetchDistance = 3,
            enablePlaceholders = false
        )

        return Pager(pagingConfig) {
            PostReplyPagingSource(postId)
        }.flow
    }
}