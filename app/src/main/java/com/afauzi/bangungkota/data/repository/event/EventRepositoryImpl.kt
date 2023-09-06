package com.afauzi.bangungkota.data.repository.event

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.afauzi.bangungkota.domain.model.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import java.util.*

class EventRepositoryImpl: EventRepository {
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

    private fun uploadMediaToFireStore(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = UUID.randomUUID()
        val mediaRef = storageRef.child("/event/${fileName}.jpg") // Ganti dengan nama yang sesuai

        mediaRef.putFile(uri)
            .addOnSuccessListener {
                mediaRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener {
                    onFailure(it)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
    private fun uploadDataEvent(data: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("events").document(data.id) // Ganti dengan nama yang sesuai

        db.runTransaction { transaction ->
            transaction.set(docRef, data)
            null // Transaksi berhasil, kembalikan null
        }
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



    override fun getEvent(documentId: String): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun updateEvent(documentId: String, data: Event): Task<Void> {
        TODO("Not yet implemented")
    }

    private fun deleteDataFromFirestore(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("events").document(documentId) // Ganti dengan nama yang sesuai

        docRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    private fun deleteMediaFromStorage(mediaUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mediaUrl)

        storageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    override fun deleteEvent(documentId: String, mediaUrl: String, onComplete: (Boolean) -> Unit) {
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