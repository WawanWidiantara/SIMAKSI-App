package com.example.simaksigunung

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.BottomSheet.BottomSheetAddMember
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListener
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityPesanJalurBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PesanJalur : AppCompatActivity(), BottomSheetListener {
    private lateinit var binding: ActivityPesanJalurBinding
    private var bottomSheetDialog: BottomSheetAddMember? = null
    var getTanggalNaik = ""
    var getTanggalTurun = ""
    private var tripId: Int = -1  // Replace with the actual trip ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanJalurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidNetworking.initialize(this)

        // Retrieve the tripId from the intent
        tripId = intent.getIntExtra("tripId", -1)

        binding.edTanggalNaik.setOnClickListener {
            showDatePicker(this) { _, year, month, dayOfMonth ->
                val formattedDateEd = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                binding.edTanggalNaik.setText(formattedDateEd)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                getTanggalNaik = formattedDate
            }
        }

        binding.edTanggalTurun.setOnClickListener {
            showDatePicker(this) { _, year, month, dayOfMonth ->
                val formattedDateEd = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                binding.edTanggalTurun.setText(formattedDateEd)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                getTanggalTurun = formattedDate
            }
        }

        binding.spinnerMetodePembayaran.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    binding.spinnerMetodePembayaran.setBackgroundResource(R.drawable.bg_ed_unclicked)
                }
            }

        binding.btnAddPersonel.setOnClickListener {
            dialogBottomSheet()
        }

        binding.btnPesan.setOnClickListener {
            startActivity(Intent(this, PesanJalurSukses::class.java))
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showDatePicker(context: Context, listener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, listener, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun dialogBottomSheet() {
        bottomSheetDialog = BottomSheetAddMember()
        bottomSheetDialog!!.setBottomSheetListener(this)
        bottomSheetDialog!!.show(supportFragmentManager, bottomSheetDialog!!.tag)
        bottomSheetDialog!!.dialog?.setOnShowListener {
            val dialog = bottomSheetDialog!!.dialog as BottomSheetDialog
            val container =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.parent as View
            container.setBackgroundColor(Color.parseColor("#80000000")) // Warna hitam transparan dengan alpha 128
        }
    }

    private fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
    }

    override fun onMemberAdded(memberId: Int) {
        addMember(memberId)
    }

    private fun addMember(memberId: Int) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        val body = JSONObject().apply {
            put("user_id", memberId)
        }

        AndroidNetworking.post("$url/api/members/")
            .addJSONObjectBody(body)
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Toast.makeText(this@PesanJalur, "Member added successfully!", Toast.LENGTH_SHORT).show()
                    dismissBottomSheet()
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(this@PesanJalur, "Failed to add member: ${anError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
