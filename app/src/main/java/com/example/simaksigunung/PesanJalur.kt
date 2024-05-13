package com.example.simaksigunung

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.BottomSheet.BottomSheetAddMember
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListener
import com.example.simaksigunung.databinding.ActivityPesanJalurBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PesanJalur : AppCompatActivity(), BottomSheetListener {
    private lateinit var binding: ActivityPesanJalurBinding
    private var bottomSheetDialog: BottomSheetAddMember? = null
    var getTanggalNaik = ""
    var getTanggalTurun = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanJalurBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    fun showDatePicker(context: Context, listener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, listener, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    private fun dialogBottomSheet() {
        bottomSheetDialog = BottomSheetAddMember()
        bottomSheetDialog!!.show(supportFragmentManager, bottomSheetDialog!!.tag)
        bottomSheetDialog!!.dialog?.setOnShowListener {
            val dialog = bottomSheetDialog!!.dialog as BottomSheetDialog

            // Set transparansi pada latar belakang di luar bottom sheet
            val container =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.parent as View
            container.setBackgroundColor(Color.parseColor("#80000000")) // Warna hitam transparan dengan alpha 128
        }
    }

    private fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
    }
}