package com.example.simaksigunung

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivityLoginBinding
import com.example.simaksigunung.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, PersonalData::class.java))
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}