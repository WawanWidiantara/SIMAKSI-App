package com.example.simaksigunung.history

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simaksigunung.databinding.ActivityQrBinding
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder

class QR : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("user_id")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        generateQRCode(userId)

        binding.btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun generateQRCode(data: String) {
        val qrgEncoder = QRGEncoder(data, null, QRGContents.Type.TEXT, 500)
        try {
            val bitmap: Bitmap = qrgEncoder.bitmap
            binding.qrImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to generate QR Code: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
