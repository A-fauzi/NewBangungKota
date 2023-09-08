package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.post.PostRepository
import com.afauzi.bangungkota.data.repository.post.reply.PostReplyParentRepository
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PostReplyViewModel @Inject constructor(
    private val replyPostReplyParentRepository: PostReplyParentRepository<Post.ReplyPost>,
) : ViewModel() {

//    val getEvents = replyPostReplyParentRepository.postPagingSource().cachedIn(viewModelScope)

    fun createPost(data: Post.ReplyPost, documentId: String): Task<Void> {
        return replyPostReplyParentRepository.createPost(data, documentId)
    }

//    fun deletePost(documentId: String): Task<Void> {
//        return replyPostReplyParentRepository.deletePost(documentId)
//    }
}