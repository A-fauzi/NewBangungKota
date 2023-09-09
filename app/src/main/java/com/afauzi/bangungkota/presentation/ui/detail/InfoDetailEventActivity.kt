package com.afauzi.bangungkota.presentation.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityInfoDetailEventBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.domain.state.ResponseState
import com.afauzi.bangungkota.presentation.viewmodels.EventViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.transformationlayout.TransformationAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoDetailEventActivity : TransformationAppCompatActivity() {

    private lateinit var binding: ActivityInfoDetailEventBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private val userViewModel: UserViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    private fun init() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.toolAppBar.topAppBar.title = "Event Detal ✨"
        binding.toolAppBar.topAppBar.menu.findItem(R.id.user).isVisible = false

        binding.toolAppBar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        receivedDataAndSetView()
    }

    private fun receivedDataAndSetView() {

        // Received Data Intent
        val receivedDataEvent = intent.getParcelableExtra("event_data") as? Event
        if (receivedDataEvent != null) {

            // Get data user by id
            lifecycleScope.launch {
                userViewModel.getUserById(receivedDataEvent.createdBy)
                    .addOnSuccessListener {
                        if (it.exists()) {

                            Glide.with(this@InfoDetailEventActivity)
                                .load(it.getString("photo"))
                                .placeholder(R.drawable.image_placeholder)
                                .error(R.drawable.image_error)
                                .into(binding.ivProfile)

                            binding.tvUsername.text = it.getString("name")
                            binding.tvUserEmail.text = it.getString("email")


                        } else {
                            Toast.makeText(
                                this@InfoDetailEventActivity,
                                "Data user tidak ditemukan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@InfoDetailEventActivity,
                            "error fetch user ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            // Get count user create event
            userCreateEventSize(receivedDataEvent.createdBy)

            // Set data event to views
            binding.tvUserAddress.text = receivedDataEvent.address
            Glide.with(this@InfoDetailEventActivity)
                .load(receivedDataEvent.image)
                .placeholder(R.drawable.image_profile_place_holder)
                .error(R.drawable.image_error)
                .into(binding.ivEvent)
            binding.tvTitleEvent.text = receivedDataEvent.title
            binding.tvDateEvent.text = receivedDataEvent.date
            binding.tvDescEvent.text = receivedDataEvent.description

        } else {
            Toast.makeText(
                this@InfoDetailEventActivity,
                "Received data is null!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun userCreateEventSize(uid: String) {
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