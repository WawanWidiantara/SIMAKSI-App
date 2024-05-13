package com.example.simaksigunung

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivitySyaratKetentuanBinding

class SyaratKetentuan : AppCompatActivity() {
    private lateinit var binding: ActivitySyaratKetentuanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyaratKetentuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelanjutnya.setOnClickListener {
            startActivity(Intent(this, PesanJalur::class.java))
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}