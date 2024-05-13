package com.example.simaksigunung

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivityDetailPembayaranBinding
import com.example.simaksigunung.databinding.ActivityDetailRuteBinding

class DetailPembayaran : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPembayaranBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}