package com.afauzi.bangungkota.data.repository.post

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class PostRepositoryImpl: PostRepository {
    private val db = FirebaseFirestore.getInstance()
    override fun postPagingSource(): Flow<PagingData<Post>> {
        val pagingConfig = PagingConfig(
            pageSize = 20, // Jumlah item per halaman
            prefetchDistance = 3, // Jumlah item yang akan diambil sebelum akhir halaman saat scroll
            enablePlaceholders = false // Apakah item-placeholder diaktifkan
        )
        val pager = Pager(pagingConfig) {
            PostPagingSource()
        }.flow

        return pager
    }

    override fun createPost(data: Post, documentId: String): Task<Void> {
        return db.collection("posts")
            .document(documentId)
            .set(data)
    }

    override fun deletePost(documentId: String): Task<Void> {
        return db.collection("posts")
            .document(documentId)
            .delete()
    }
}