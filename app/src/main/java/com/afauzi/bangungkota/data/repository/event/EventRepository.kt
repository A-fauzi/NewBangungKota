package com.afauzi.bangungkota.data.repository.event

import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Event
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun eventPagingSource(): Flow<PagingData<Event>>

    fun createEvent(documentId: String, data: Event): Task<Void>

    fun getEvent(documentId: String): Task<Void>

    fun updateEvent(documentId: String, data: Event): Task<Void>

    fun deleteEvent(documentId: String): Task<Void>
}