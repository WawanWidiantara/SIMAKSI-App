package com.example.simaksigunung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityDetailTransaksiBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class DetailTransaksi : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransaksiBinding
    private val dateFormatDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormatServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra("id_order")
        val statusOrder = intent.getStringExtra("status_order")
        if (tripId != null) {
            try {
                val tripIdInt = tripId.replace("ID: ", "").toInt()
                fetchTripDetails(tripIdInt, statusOrder)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid Trip ID format", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Invalid Trip ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnPembayaran.setOnClickListener {
            val paymentDetails = it.tag as? String
            val intent = Intent(this, DetailPembayaran::class.java).apply {
                putExtra("payment_details", paymentDetails)
            }
            startActivity(intent)
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchTripDetails(tripId: Int, statusOrder: String? = null) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        AndroidNetworking.get("$url/api/trips/$tripId")
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        try {
                            val data = it.getJSONObject("data")
                            bindTripDetails(data, statusOrder)
                        } catch (e: Exception) {
                            Log.e("DetailTransaksi", "Error parsing response: ${e.message}")
                            Toast.makeText(this@DetailTransaksi, "Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e("DetailTransaksi", "Error fetching trip details: ${anError.errorDetail}")
                    Toast.makeText(this@DetailTransaksi, "Failed to fetch trip details: ${anError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun bindTripDetails(data: JSONObject, statusOrder: String? = null) {
        try {
            binding.idPesanan.text = data.optInt("id", -1).takeIf { it != -1 }?.toString() ?: "-"
            binding.tanggalPesanan.text = formatDate(data.optString("created_at"))
            binding.namaPemesan.text = data.optJSONObject("route")?.optString("name") ?: "-"
            binding.metodePembayaran.text = data.optJSONObject("payment")?.optString("bank") ?: "-"
            binding.checkIn.text = formatNullableDate(data.optString("checked_in_at"))
            binding.checkOut.text = formatNullableDate(data.optString("checked_out_at"))

            val anggotaList = data.optJSONArray("members")
            val anggotaNames = mutableListOf<String>()
            if (anggotaList != null) {
                for (i in 0 until anggotaList.length()) {
                    val member = anggotaList.optJSONObject(i)
                    val userName = member?.optJSONObject("user")?.optString("name") ?: "-"
                    anggotaNames.add("- $userName")
                }
            } else {
                anggotaNames.add("-")
            }
            binding.anggota.text = anggotaNames.joinToString("\n")

            val status = "Status ${capitalizeWords(statusOrder ?: "-")}"
            binding.statusTransaksi.text = status

            when (statusOrder?.lowercase()) {
                "menunggu" -> {
                    binding.btnPembayaran.visibility = View.VISIBLE
                    binding.btnBatalkan.visibility = View.VISIBLE
                    binding.btnCetak.visibility = View.GONE
                }
                "lunas" -> {
                    binding.btnPembayaran.visibility = View.GONE
                    binding.btnBatalkan.visibility = View.GONE
                    binding.btnCetak.visibility = View.VISIBLE
                }
                else -> {
                    binding.btnPembayaran.visibility = View.GONE
                    binding.btnBatalkan.visibility = View.GONE
                    binding.btnCetak.visibility = View.GONE
                }
            }

            // Pass payment details to the button tag for use in the intent
            binding.btnPembayaran.tag = data.optJSONObject("payment")?.toString() ?: "{}"
        } catch (e: Exception) {
            Log.e("DetailTransaksi", "Error binding data: ${e.message}")
            Toast.makeText(this, "Error binding data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(dateString: String?): String {
        return try {
            val date = dateFormatServer.parse(dateString ?: return "-")
            dateFormatDisplay.format(date!!)
        } catch (e: Exception) {
            "-"
        }
    }

    private fun formatNullableDate(dateString: String?): String {
        return if (dateString == null || dateString == "null") {
            "-"
        } else {
            formatDate(dateString)
        }
    }

    private fun capitalizeWords(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }
}
