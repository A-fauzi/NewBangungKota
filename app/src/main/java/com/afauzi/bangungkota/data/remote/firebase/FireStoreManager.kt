package com.afauzi.bangungkota.data.remote.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class FireStoreManager<T: Any>(private val collectionPath: String) {
    private val db = FirebaseFirestore.getInstance()

    fun create(data: T, documentId: String): Task<Void> {
        return db.collection(collectionPath)
            .document(documentId)
            .set(data)
    }

    fun createTransaction(data: T, documentId: String): Task<Transaction> {
        return db.runTransaction { transaction ->
            transaction.set(db.collection(collectionPath).document(documentId), data)
            null // Transaksi berhasil, kembalikan null
        }
    }

    fun getData(documentId: String): Task<DocumentSnapshot> {
        return db.collection(collectionPath)
            .document(documentId)
            .get()
    }

    fun update(data: T, documentId: String): Task<Void> {
        return db.collection(collectionPath)
            .document(documentId)
            .set(data, SetOptions.merge())
    }

    fun delete(documentId: String): Task<Void> {
        return db.collection(collectionPath)
            .document(documentId)
            .delete()
    }
}