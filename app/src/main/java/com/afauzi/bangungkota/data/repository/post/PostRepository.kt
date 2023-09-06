package com.afauzi.bangungkota.data.repository.post

import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun postPagingSource(): Flow<PagingData<Post>>
}