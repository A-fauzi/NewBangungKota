package com.afauzi.bangungkota.data.repository.event

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun eventPagingSource(): Flow<PagingData<Event>>

    fun createEventDataAndMediaFireStore(uri: Uri, data: Event, onComplete: (Boolean) -> Unit)

//    suspend fun getEvent(documentId: String): Task<DocumentSnapshot>
    suspend fun getEvent(documentId: String): MutableLiveData<ResponseState<Event>>

    fun updateEvent(documentId: String, data: Event): Task<Void>

    fun deleteEventDataAndMediaFireStore(documentId: String, mediaUrl: String, onComplete: (Boolean) -> Unit)
}