package com.afauzi.bangungkota.presentation.ui.event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afauzi.bangungkota.databinding.ActivityCreateEventBinding

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}