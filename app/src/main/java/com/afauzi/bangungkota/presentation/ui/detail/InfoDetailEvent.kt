package com.afauzi.bangungkota.presentation.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityInfoDetailEventBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoDetailEvent : AppCompatActivity() {

    private lateinit var binding: ActivityInfoDetailEventBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedData = intent.getParcelableExtra("event_data") as? Event
        if (receivedData != null) {
            Toast.makeText(this, receivedData.id, Toast.LENGTH_SHORT).show()
        }
    }
}