package com.afauzi.bangungkota.data.repository.post.reply

import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface PostReplyParentRepository<T: Any> {
    fun replyPostPagingSource(postId: String): Flow<PagingData<T>>
    fun replyPostChildPagingSource(postId: String): Flow<PagingData<T>>
    fun createPost(collection: String, data: Post.ReplyPost, documentId: String): Task<Void>
    fun deletePost(documentId: String): Task<Void>
}