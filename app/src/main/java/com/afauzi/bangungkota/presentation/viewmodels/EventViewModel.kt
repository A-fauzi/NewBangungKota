package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class EventViewModel: ViewModel() {
    private val eventRepository: EventRepository = EventRepository()
    val getEvents = eventRepository.eventPagingSource().cachedIn(viewModelScope)
}