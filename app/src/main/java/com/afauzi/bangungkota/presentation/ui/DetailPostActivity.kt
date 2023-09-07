package com.afauzi.bangungkota.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedData = intent.getParcelableExtra("post_data") as? Post
        if (receivedData != null) {

            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

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
        }
    }


}