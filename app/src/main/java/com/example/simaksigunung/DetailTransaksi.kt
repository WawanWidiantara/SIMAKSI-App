package com.example.simaksigunung

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivityDetailRuteBinding
import com.example.simaksigunung.databinding.ActivityDetailTransaksiBinding

class DetailTransaksi : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransaksiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPembayaran.setOnClickListener {
            val intent = Intent(this, DetailPembayaran::class.java)
            startActivity(intent)
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}