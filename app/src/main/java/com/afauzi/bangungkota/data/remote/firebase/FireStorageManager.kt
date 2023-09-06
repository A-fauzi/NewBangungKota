package com.afauzi.bangungkota.data.remote.firebase

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class FireStorageManager(private val fileName: String) {
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val ref = firebaseStorage.reference
    private val mediaReference = ref.child(fileName)

    fun uploadMediaImage(
        uri: Uri, onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ): StorageTask<UploadTask.TaskSnapshot> {
        return mediaReference.putFile(uri)
            .addOnSuccessListener {
                mediaReference.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener {
                    onFailure(it)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteMediaImage(
        mediaUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ): Task<Void> {
        val storageRef = firebaseStorage.getReferenceFromUrl(mediaUrl)
        return storageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}