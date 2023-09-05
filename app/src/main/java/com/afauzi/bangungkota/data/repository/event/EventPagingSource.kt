package com.afauzi.bangungkota.data.repository.event

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afauzi.bangungkota.domain.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class EventPagingSource: PagingSource<QuerySnapshot, Event>() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Event>): QuerySnapshot? {
        // Gunakan state.anchorPosition atau data terakhir yang diambil
        // sebagai kunci penyegaran (refresh key)
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Event> {
        return try {
            val currentPage = params.key ?: db.collection("events")
                .orderBy("createdAt")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents.lastOrNull()
            val nextPage = db.collection("events")
                .orderBy("createdAt")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val events = currentPage.toObjects(Event::class.java)

            Log.d("EventDataSource", events.toString())

            LoadResult.Page(
                data = events.reversed(),
                prevKey = null,
                nextKey = if (nextPage.isEmpty) null else nextPage
            )
        } catch (e: Exception) {
            Log.d("EventDataSource", e.message.toString())
            LoadResult.Error(e)
        }

    }
}