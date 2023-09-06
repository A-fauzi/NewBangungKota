package com.afauzi.bangungkota.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.data.repository.event.EventRepositoryImpl
import com.afauzi.bangungkota.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class EventViewModel: ViewModel() {
    private val eventRepository: EventRepository = EventRepositoryImpl()
    val getEvents = eventRepository.eventPagingSource().cachedIn(viewModelScope)

    fun createEvent(uri: Uri, data: Event, onComplete: (Boolean) -> Unit) {
        eventRepository.createEventDataAndMediaFireStore(uri, data, onComplete)
    }

    fun deleteEvent(documentId: String, mediaUrl: String, onComplete: (Boolean) -> Unit) {
        eventRepository.deleteEvent(documentId, mediaUrl, onComplete)
    }
}