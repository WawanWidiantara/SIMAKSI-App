package com.example.simaksigunung

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivityDetailRuteBinding

class DetailRute : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRuteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRuteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}