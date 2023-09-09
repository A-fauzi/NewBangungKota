package com.afauzi.bangungkota.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.data.repository.event.EventRepositoryImpl
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.domain.state.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(private val eventRepository: EventRepository): ViewModel() {

    val getEvents = eventRepository.eventPagingSource().cachedIn(viewModelScope)

    fun createEvent(uri: Uri, data: Event, onComplete: (Boolean) -> Unit) {
        eventRepository.createEventDataAndMediaFireStore(uri, data, onComplete)
    }

    private var _getEvent: MutableLiveData<ResponseState<Event>> = MutableLiveData()
    val getEvent: LiveData<ResponseState<Event>> get() = _getEvent
    fun getEventById(eventId: String) {
        viewModelScope.launch {
            _getEvent = eventRepository.getEvent(eventId)
        }
    }

    fun deleteEvent(documentId: String, mediaUrl: String, onComplete: (Boolean) -> Unit) {
        eventRepository.deleteEventDataAndMediaFireStore(documentId, mediaUrl, onComplete)
    }
}