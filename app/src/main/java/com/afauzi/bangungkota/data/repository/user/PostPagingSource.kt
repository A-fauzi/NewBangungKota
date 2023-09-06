package com.afauzi.bangungkota.data.repository.user

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afauzi.bangungkota.domain.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PostPagingSource: PagingSource<QuerySnapshot, Post>() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        // Gunakan state.anchorPosition atau data terakhir yang diambil
        // sebagai kunci penyegaran (refresh key)
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val currentPage = params.key ?: db.collection("posts")
                .orderBy("created_at")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents.lastOrNull()
            val nextPage = db.collection("posts")
                .orderBy("created_at")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val post = currentPage.toObjects(Post::class.java)

            Log.d("PostDataSource", post.toString())

            LoadResult.Page(
                data = post.reversed(),
                prevKey = null,
                nextKey = if (nextPage.isEmpty) null else nextPage
            )
        } catch (e: Exception) {
            Log.d("PostDataSource", e.message.toString())
            LoadResult.Error(e)
        }

    }
}