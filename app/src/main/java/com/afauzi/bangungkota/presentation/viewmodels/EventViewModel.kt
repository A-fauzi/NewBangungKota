package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.data.repository.event.EventRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class EventViewModel: ViewModel() {
    private val eventRepository: EventRepository = EventRepositoryImpl()
    val getEvents = eventRepository.eventPagingSource().cachedIn(viewModelScope)
}