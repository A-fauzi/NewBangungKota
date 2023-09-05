package com.afauzi.bangungkota.data.repository.event

import android.net.Uri
import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun eventPagingSource(): Flow<PagingData<Event>>

    fun createEventDataAndMediaFireStore(uri: Uri, data: Event, onComplete: (Boolean) -> Unit)

    fun getEvent(documentId: String): Task<Void>

    fun updateEvent(documentId: String, data: Event): Task<Void>

    fun deleteEvent(documentId: String): Task<Void>
}