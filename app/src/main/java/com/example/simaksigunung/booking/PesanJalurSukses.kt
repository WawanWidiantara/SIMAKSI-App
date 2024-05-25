package com.example.simaksigunung.booking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.simaksigunung.MainActivity
import com.example.simaksigunung.databinding.ActivityPesanJalurSuksesBinding

class PesanJalurSukses : AppCompatActivity() {
    private lateinit var binding: ActivityPesanJalurSuksesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanJalurSuksesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnOke.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}