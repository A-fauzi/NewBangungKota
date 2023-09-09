package com.afauzi.bangungkota.data.repository.post.reply

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afauzi.bangungkota.domain.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PostReplyPagingSource(private val postId: String): PagingSource<QuerySnapshot, Post.ReplyPost>() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post.ReplyPost>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post.ReplyPost> {
        return try {
            val currentPage = params.key ?: db.collection("reply_post_parent")
                .whereEqualTo("postId", postId)
                .orderBy("created_at")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents.lastOrNull()

            val nextPage = db.collection("reply_post_parent")
                .whereEqualTo("postId", postId)
                .orderBy("created_at")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val replyPostParent = currentPage.toObjects(Post.ReplyPost::class.java)

            Log.d("PostReplyPagingSource", replyPostParent.toString())

            LoadResult.Page(
                data = replyPostParent.reversed(),
                prevKey = null,
                nextKey = if (nextPage.isEmpty) null else nextPage
            )
        }catch (e: Exception) {
            Log.d("PostReplyPagingSource", e.message.toString())
            LoadResult.Error(e)
        }
    }
}