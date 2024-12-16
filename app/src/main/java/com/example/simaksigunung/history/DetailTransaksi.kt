package com.example.simaksigunung.history

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.MainActivity
import com.example.simaksigunung.R
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityDetailTransaksiBinding
import com.example.simaksigunung.databinding.DialogDibatalkanBinding
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class DetailTransaksi : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransaksiBinding
    private val dateFormatDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormatServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
        val readImagesGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        val readVideoGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: false
        val readAudioGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: false

        if (writeGranted || readImagesGranted || readVideoGranted || readAudioGranted) {
            generatePDF()
        } else {
            Toast.makeText(this, "Permission denied. Cannot download PDF.", Toast.LENGTH_SHORT).show()
        }
    }

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

        binding.btnBatalkan.setOnClickListener {
            showCancelDialog()
        }

        binding.btnCetak.setOnClickListener {
            checkPermissionsAndDownloadPDF()
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

        binding.btnGenerateQR.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            val userId = sharedPreferences?.getInt("user_id", -1) ?: -1

            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, QR::class.java)
            intent.putExtra("trip_id", tripId.toString())
            startActivity(intent)
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
                    binding.btnGenerateQR.visibility = View.GONE
                }
                "lunas" -> {
                    binding.btnPembayaran.visibility = View.GONE
                    binding.btnBatalkan.visibility = View.GONE
                    binding.btnCetak.visibility = View.VISIBLE
                    binding.btnGenerateQR.visibility = View.VISIBLE
                }"aktif" -> {
                    binding.btnPembayaran.visibility = View.GONE
                    binding.btnBatalkan.visibility = View.GONE
                    binding.btnCetak.visibility = View.VISIBLE
                    binding.btnGenerateQR.visibility = View.VISIBLE
                }
                else -> {
                    binding.btnPembayaran.visibility = View.GONE
                    binding.btnBatalkan.visibility = View.GONE
                    binding.btnCetak.visibility = View.GONE
                    binding.btnGenerateQR.visibility = View.GONE
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

    private fun showCancelDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_dibatalkan, null)
        val dialogBinding = DialogDibatalkanBinding.bind(dialogView)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogBinding.btnYa.setOnClickListener {
            dialogBinding.progressBar.visibility = View.VISIBLE
            dialogBinding.btnYa.visibility = View.GONE
            cancelOrder(alertDialog, dialogBinding.progressBar)
        }

        dialogBinding.btnTidak.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun cancelOrder(alertDialog: AlertDialog, progressBar: View) {
        val tripId = intent.getStringExtra("id_order")?.replace("ID: ", "")?.toIntOrNull() ?: return
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        val url = urlAPI.endPoint.url
        AndroidNetworking.post("$url/api/trips/$tripId/cancel")
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Toast.makeText(this@DetailTransaksi, "Order cancelled successfully", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()

                    // Navigate to History fragment
                    val intent = Intent(this@DetailTransaksi, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("fragment", "history")
                    startActivity(intent)
                }

                override fun onError(anError: ANError) {
                    Log.e("DetailTransaksi", "Error cancelling order: ${anError.errorDetail}")
                    Toast.makeText(this@DetailTransaksi, "Failed to cancel order: ${anError.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun checkPermissionsAndDownloadPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                ))
            } else {
                generatePDF()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            } else {
                generatePDF()
            }
        }
    }

    private fun generatePDF() {
        val tripId = intent.getStringExtra("id_order")?.replace("ID: ", "")?.toIntOrNull() ?: return
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        val pdfUrl = "$url/api/trips/$tripId/generate_pdf/"

        val fileName = "trip_$tripId.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        AndroidNetworking.download(pdfUrl, file.parent, file.name)
            .addHeaders("Authorization", userId)
            .build()
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(this@DetailTransaksi, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
                    openPDF(file)
                }

                override fun onError(anError: ANError) {
                    Log.e("DetailTransaksi", "Error downloading PDF: ${anError.errorDetail}")
                    Toast.makeText(this@DetailTransaksi, "Failed to download PDF: ${anError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openPDF(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        val chooser = Intent.createChooser(intent, "Open with")
        startActivity(chooser)
    }
}
