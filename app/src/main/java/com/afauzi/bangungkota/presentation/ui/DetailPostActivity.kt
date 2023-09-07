package com.afauzi.bangungkota.presentation.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.adapter.AdapterCommentPost
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.afauzi.bangungkota.utils.UniqueIdGenerator
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val receivedData = intent.getParcelableExtra("post_data") as? Post
        if (receivedData != null) {

            getListComment(receivedData.id)

            lifecycleScope.launch {
                userViewModel.getUserById(receivedData.uid.toString())
                    .addOnSuccessListener {
                        if (it.exists()) {
                            Glide.with(this@DetailPostActivity)
                                .load(it.getString("photo"))
                                .placeholder(R.drawable.image_profile_place_holder)
                                .error(R.drawable.image_error)
                                .into(binding.itemPost.itemIvProfile)
                            binding.itemPost.itemNameUser.text = it.getString("name")
                            binding.itemPost.itemEmailUser.text = it.getString("email")
                            binding.itemPost.tvTextPost.text = receivedData.text

                            binding.itemPost.btnMorePost.isVisible = receivedData.uid == user?.uid
                        } else {
                            // Handle data not exists
                        }
                    }.addOnFailureListener {
                        // Handle request failure
                    }
            }

            binding.outlineTextfieldCommentMessage.setEndIconOnClickListener {
                val etTextComment = binding.etPostComment

                insertDataPost(
                    UniqueIdGenerator.generateUniqueId(),
                    receivedData.id,
                    user?.uid.toString(),
                    etTextComment.text.toString().trim(),
                )

                etTextComment.text?.clear()

                // Di dalam fungsi Anda yang mengirim pesan (setelah pesan berhasil dikirim):
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(etTextComment.windowToken, 0)
            }

        }


        Glide.with(this@DetailPostActivity)
            .load(user?.photoUrl)
            .placeholder(R.drawable.image_profile_place_holder)
            .error(R.drawable.image_error)
            .into(binding.ivCurrentUserMessage)




    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertDataPost(commentId: String, postId: String, currentUserId: String, text: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val tanggalSaatIni = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val tanggalDalamFormatQuery = tanggalSaatIni.format(formatter)
        val data = Post.ReplyPost(commentId, postId, currentUserId, text, tanggalDalamFormatQuery)

        // Specify the database reference where you want to store the data
        val postCommentRef = databaseReference.child("comments").child(postId).child(data.id.toString())

        postCommentRef.setValue(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "success reply", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "not success reply", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "error ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun getListComment(postId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val postCommentRef = databaseReference.child("comments").child(postId)

        // Membuat daftar kosong untuk menyimpan data
        val commentPost = mutableListOf<Post.ReplyPost>()
        val commentAdapter = AdapterCommentPost(commentPost) {viewBinding, data ->
            viewBinding.tvTextPost.text = data.text
            lifecycleScope.launch {
                userViewModel.getUserById(data.userId.toString())
                    .addOnSuccessListener {
                        Glide.with(this@DetailPostActivity)
                            .load(it.getString("photo"))
                            .into(viewBinding.itemIvProfile)
                        viewBinding.itemNameUser.text = it.getString("name")
                        viewBinding.itemEmailUser.text = it.getString("email")
                    }
                    .addOnFailureListener {  }
            }
        }
        binding.rvReply.apply {
            layoutManager = LinearLayoutManager(this@DetailPostActivity, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
        }

        postCommentRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Bersihkan daftar sebelum mengisi ulang
                commentPost.clear()

                // Mengambil data dari snapshot
                for (comment in snapshot.children) {
                    // Konversi snapshot menjadi objek UserData
                    val dataComment = comment.getValue(Post.ReplyPost::class.java)

                    // Jika userData tidak null, tambahkan ke daftar
                    dataComment?.let {
                        commentPost.add(it)
                    }
                }

                // Sekarang userList berisi daftar objek UserData dari database
                // Lakukan apa pun yang diperlukan dengan daftar ini, seperti menampilkannya di UI

                commentPost.reverse()
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle kesalahan jika ada
            }


        })
    }
}