package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.post.reply.PostReplyParentRepository
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class PostReplyViewModel @Inject constructor(
    private val replyPostReplyParentRepository: PostReplyParentRepository<Post.ReplyPost>,
) : ViewModel() {

    fun getReplyPost(postId: String) = replyPostReplyParentRepository.replyPostPagingSource(postId).cachedIn(viewModelScope)
    
    fun getReplyPostChild(postId: String) = replyPostReplyParentRepository.replyPostChildPagingSource(postId).cachedIn(viewModelScope)

    fun createPostReply(collection: String, data: Post.ReplyPost, documentId: String): Task<Void> {
        return replyPostReplyParentRepository.createPost(collection, data, documentId)
    }

//    fun deletePost(documentId: String): Task<Void> {
//        return replyPostReplyParentRepository.deletePost(documentId)
//    }
}