package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.post.PostRepository
import com.afauzi.bangungkota.data.repository.post.PostRepositoryImpl

class PostViewModel: ViewModel() {
    private val postRepository: PostRepository = PostRepositoryImpl()
    val getEvents = postRepository.postPagingSource().cachedIn(viewModelScope)
}