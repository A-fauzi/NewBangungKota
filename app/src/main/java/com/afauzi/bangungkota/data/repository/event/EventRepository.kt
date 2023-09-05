package com.afauzi.bangungkota.data.repository.event

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.data.remote.firebase.EventPagingSource
import com.afauzi.bangungkota.domain.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepository {
   fun eventPagingSource(): Flow<PagingData<Event>> {
       val pagingConfig = PagingConfig(
           pageSize = 20, // Jumlah item per halaman
           prefetchDistance = 3, // Jumlah item yang akan diambil sebelum akhir halaman saat scroll
           enablePlaceholders = false // Apakah item-placeholder diaktifkan
       )

       val pager = Pager(pagingConfig) {
           EventPagingSource()
       }.flow

       Log.d("EventRepository", pager.toString())

       return pager
   }
}