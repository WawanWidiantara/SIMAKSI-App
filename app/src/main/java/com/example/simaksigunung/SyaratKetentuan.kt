package com.example.simaksigunung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivitySyaratKetentuanBinding

class SyaratKetentuan : AppCompatActivity() {
    private lateinit var binding: ActivitySyaratKetentuanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(this)
        binding = ActivitySyaratKetentuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelanjutnya.setOnClickListener {
            postTrip()
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun postTrip() {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1).toString()

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url  // Replace with your actual API endpoint

        AndroidNetworking.post("$url/api/trips")
            .addHeaders("Authorization", userId)
            .setContentType("application/json")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    // Handle the response
                    Toast.makeText(this@SyaratKetentuan, "Trip created successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SyaratKetentuan, PesanJalur::class.java))
                }

                override fun onError(anError: ANError?) {
                    // Handle the error
                    Toast.makeText(this@SyaratKetentuan, "Failed to create trip: ${anError?.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
