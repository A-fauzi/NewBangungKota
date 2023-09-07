package com.afauzi.bangungkota.presentation.ui.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afauzi.bangungkota.databinding.ActivityCameraStoryBinding


class CameraStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}