package com.afauzi.bangungkota.data.repository.post

import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun postPagingSource(): Flow<PagingData<Post>>
    fun createPost(data: Post, documentId: String): Task<Void>
}