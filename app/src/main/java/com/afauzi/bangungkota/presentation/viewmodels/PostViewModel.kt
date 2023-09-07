package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.post.PostRepository
import com.afauzi.bangungkota.data.repository.post.PostRepositoryImpl
import com.afauzi.bangungkota.data.repository.post.reply.ReplyPostRepository
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val replyPostRepository: ReplyPostRepository
) : ViewModel() {

    val getEvents = postRepository.postPagingSource().cachedIn(viewModelScope)

    fun createPost(data: Post, documentId: String): Task<Void> {
        return postRepository.createPost(data, documentId)
    }

    fun deletePost(documentId: String): Task<Void> {
        return postRepository.deletePost(documentId)
    }

    // REPLY POST
    fun createReplyPost(data: Post.ReplyPost, postId: String): Task<Void> {
        return replyPostRepository.createReplyPost(data, postId)
    }
}