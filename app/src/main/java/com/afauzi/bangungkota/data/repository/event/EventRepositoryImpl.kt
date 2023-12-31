package com.afauzi.bangungkota.data.repository.event

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.data.remote.firebase.FireStorageManager
import com.afauzi.bangungkota.data.remote.firebase.FireStoreManager
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.domain.state.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import java.util.*

class EventRepositoryImpl : EventRepository {
    private val fileName = UUID.randomUUID()
    private val fireStoreManager: FireStoreManager<Event> = FireStoreManager("events")
    private val fireStorageManager: FireStorageManager = FireStorageManager("/events/${fileName}.jpg")

    override fun eventPagingSource(): Flow<PagingData<Event>> {
        val pagingConfig = PagingConfig(
            pageSize = 20, // Jumlah item per halaman
            prefetchDistance = 3, // Jumlah item yang akan diambil sebelum akhir halaman saat scroll
            enablePlaceholders = false // Apakah item-placeholder diaktifkan
        )
        val pager = Pager(pagingConfig) {
            EventPagingSource()
        }.flow

        return pager
    }

    private fun uploadMediaToFireStore(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fireStorageManager.uploadMediaImage(uri, onSuccess, onFailure)
    }

    private fun uploadDataEvent(
        data: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        fireStoreManager.createTransaction(data, data.id.toString())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    override fun createEventDataAndMediaFireStore(
        uri: Uri,
        data: Event,
        onComplete: (Boolean) -> Unit
    ) {
        uploadMediaToFireStore(uri, { mediaUrl ->
            data.image = mediaUrl // Menghubungkan URL media dengan data Firestore
            uploadDataEvent(data, {
                onComplete(true) // Keduanya berhasil
            }, {
                onComplete(false) // Salah satu operasi gagal
            })
        }, {
            onComplete(false) // Salah satu operasi gagal
        })
    }


    override suspend fun getEvent(documentId: String): MutableLiveData<ResponseState<Event>> {
        val getEventLiveData: MutableLiveData<ResponseState<Event>> = MutableLiveData()
        fireStoreManager.getData(documentId)
            .addOnSuccessListener {
                if (it.exists()) {
                    if (it.getTimestamp("createdAt") != null && it.getTimestamp("updatedAt") != null) {
                        val event = Event(
                            id = it.getString("id"),
                            title = it.getString("title").toString(),
                            description = it.getString("description").toString(),
                            address = it.getString("address").toString(),
                            image = it.getString("image").toString(),
                            date = it.getString("date").toString(),
                            time = it.getString("time").toString(),
                            createdAt = it.getTimestamp("createdAt")!!,
                            updatedAt = it.getTimestamp("updatedAt")!!,
                            createdBy = it.getString("createdBy").toString()
                        )

                        getEventLiveData.value = ResponseState.Success(event)
                    } else {
                        getEventLiveData.value = ResponseState.Error("data timestamp error")
                    }
                } else {
                    getEventLiveData.value = ResponseState.Error("Data Event Not Exists")
                }
            }
            .addOnFailureListener {
                getEventLiveData.value = ResponseState.Error(it.message.toString())
            }

        return getEventLiveData
    }

    override fun updateEvent(documentId: String, data: Event): Task<Void> {
        TODO("Not yet implemented")
    }

    private fun deleteDataFromFirestore(
        documentId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {


        fireStoreManager.delete(documentId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }

    }

    private fun deleteMediaFromStorage(
        mediaUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fireStorageManager.deleteMediaImage(mediaUrl, onSuccess, onFailure)
    }

    override fun deleteEventDataAndMediaFireStore(documentId: String, mediaUrl: String, onComplete: (Boolean) -> Unit) {
        deleteDataFromFirestore(documentId, {
            deleteMediaFromStorage(mediaUrl, {
                onComplete(true) // Keduanya berhasil dihapus
            }, {
                onComplete(false) // Penghapusan media gagal
            })
        }, {
            onComplete(false) // Penghapusan data dari Firestore gagal
        })
    }
}