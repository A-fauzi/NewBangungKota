package com.afauzi.bangungkota.presentation.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityInfoDetailEventBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoDetailEvent : AppCompatActivity() {

    private lateinit var binding: ActivityInfoDetailEventBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolAppBar.topAppBar.title = "Event Detal ✨"
        binding.toolAppBar.topAppBar.menu.findItem(R.id.user).isVisible = false

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        binding.toolAppBar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val receivedData = intent.getParcelableExtra("event_data") as? Event
        if (receivedData != null) {

            getEventSizeByUid(receivedData.createdBy)

            lifecycleScope.launch {
                userViewModel.getUserById(receivedData.createdBy)
                    .addOnSuccessListener {
                        if (it.exists()) {

                            Glide.with(this@InfoDetailEvent)
                                .load(it.getString("photo"))
                                .placeholder(R.drawable.image_placeholder)
                                .error(R.drawable.image_error)
                                .into(binding.ivProfile)

                            binding.tvUsername.text = it.getString("name")
                            binding.tvUserEmail.text = it.getString("email")
                            binding.tvUserAddress.text = receivedData.address
                            Glide.with(this@InfoDetailEvent)
                                .load(receivedData.image)
                                .placeholder(R.drawable.image_profile_place_holder)
                                .error(R.drawable.image_error)
                                .into(binding.ivEvent)
                            binding.tvTitleEvent.text = receivedData.title
                            binding.tvDateEvent.text = receivedData.date
                            binding.tvDescEvent.text = receivedData.description

                        } else {
                            Toast.makeText(
                                this@InfoDetailEvent,
                                "Data tidak ditemukan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@InfoDetailEvent,
                            "error fetch user ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }
    }


    fun getEventSizeByUid(uid: String) {
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Reference to the Firestore collection you want to count
        val collectionRef = db.collection("events")

        // Create a query to filter documents by UID
        val query = collectionRef.whereEqualTo("createdBy", uid) // Replace "uidField" with the actual field name in your documents

        // Perform the query to count matching documents
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val size = querySnapshot.size()
                binding.createEventSize.text = size.toString()
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the query
                println("Error getting document count: $exception")
            }
    }
}