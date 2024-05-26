package com.example.simaksigunung.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simaksigunung.databinding.ActivityDetailPembayaranBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class DetailPembayaran : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPembayaranBinding
    private val dateFormatServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paymentDetails = intent.getStringExtra("payment_details")
        paymentDetails?.let {
            try {
                val paymentData = JSONObject(it)
                bindPaymentDetails(paymentData)
            } catch (e: Exception) {
                Log.e("DetailPembayaran", "Error parsing payment details: ${e.message}")
            }
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

        binding.btnSalinVa.setOnClickListener {
            copyToClipboard(binding.nomorVa.text.toString(), "Nomor VA")
        }

        binding.btnSalinTotalBayar.setOnClickListener {
            copyToClipboard(binding.totalBayar.text.toString(), "Total Bayar")
        }
    }

    private fun bindPaymentDetails(paymentData: JSONObject) {
        try {
            binding.nomorVa.text = paymentData.optString("va_number", "-")
            binding.totalBayar.text = "Rp.${paymentData.optInt("price", 0)}"

            val expirationDate = paymentData.optString("expiration")
            Log.d("DetailPembayaran", "Expiration date string: $expirationDate")

            val expirationTime = dateFormatServer.parse(expirationDate)?.time ?: 0L
            val currentTime = System.currentTimeMillis()

            Log.d("DetailPembayaran", "Expiration time (ms): $expirationTime")
            Log.d("DetailPembayaran", "Current time (ms): $currentTime")

            val timeLeft = expirationTime - currentTime
            Log.d("DetailPembayaran", "Time left (ms): $timeLeft")

            if (timeLeft > 0) {
                startCountdown(timeLeft)
                binding.keteranganBatas.text = "Waktu pembayaran tersisa:"
            } else {
                binding.jam.text = "0"
                binding.menit.text = "0"
                binding.detik.text = "0"
                binding.keteranganBatas.text = "Waktu pembayaran telah habis"
            }
        } catch (e: Exception) {
            Log.e("DetailPembayaran", "Error binding payment details: ${e.message}")
            Toast.makeText(this, "Error binding payment details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCountdown(timeLeft: Long) {
        val timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                Log.d("DetailPembayaran", "Countdown - Hours: $hours, Minutes: $minutes, Seconds: $seconds")

                binding.jam.text = hours.toString()
                binding.menit.text = minutes.toString()
                binding.detik.text = seconds.toString()
            }

            override fun onFinish() {
                binding.jam.text = "0"
                binding.menit.text = "0"
                binding.detik.text = "0"
                binding.keteranganBatas.text = "Waktu pembayaran telah habis"
            }
        }
        timer.start()
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label disalin ke papan klip", Toast.LENGTH_SHORT).show()
    }
}
