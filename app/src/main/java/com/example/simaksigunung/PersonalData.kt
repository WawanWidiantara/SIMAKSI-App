package com.example.simaksigunung

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simaksigunung.databinding.ActivityLoginBinding
import com.example.simaksigunung.databinding.ActivityPersonalDataBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PersonalData : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDataBinding
    var getTanggalLahir = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edTanggalLahir.setOnClickListener {
            showDatePicker(this) { _, year, month, dayOfMonth ->

                val formattedDateEd = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                binding.edTanggalLahir.setText(formattedDateEd)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                getTanggalLahir = formattedDate
            }
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
}