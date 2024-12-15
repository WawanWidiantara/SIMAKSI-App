package com.example.simaksigunung.register

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.MainActivity
import com.example.simaksigunung.R
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityPersonalDataBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PersonalData : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDataBinding
    var getTanggalLahir = ""
    private var selectedGender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val confirmPassword = intent.getStringExtra("confirmPassword")

        validationEditText()

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

        binding.spinnerJenisKelamin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGender = parent.getItemAtPosition(position).toString()
                validateForm()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedGender = ""
                validateForm()
            }
        }

        binding.btnDaftar.setOnClickListener {
            if (binding.btnDaftar.isEnabled) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnDaftar.visibility = View.GONE
                register(email ?: "", password ?: "", confirmPassword ?: "")
            }
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

        // Calculate the maximum date (17 years before today)
        val maxDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -17)
        }.timeInMillis

        val datePickerDialog = DatePickerDialog(context, listener, year, month, dayOfMonth)

        // Disable future dates and set max date to 17 years ago
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }

    private fun validationEditText() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.edNik.addTextChangedListener(textWatcher)
        binding.edNama.addTextChangedListener(textWatcher)
        binding.edTanggalLahir.addTextChangedListener(textWatcher)
        binding.edTinggiBadan.addTextChangedListener(textWatcher)
        binding.edBeratBadan.addTextChangedListener(textWatcher)
        binding.edNomor.addTextChangedListener(textWatcher)
        binding.edNomorDarurat.addTextChangedListener(textWatcher)
        binding.edAlamat.addTextChangedListener(textWatcher)

    }

    private fun validateForm() {
        val nik = binding.edNik.text.toString().trim()
        val nama = binding.edNama.text.toString().trim()
        val tanggalLahir = binding.edTanggalLahir.text.toString().trim()
        val tinggiBadan = binding.edTinggiBadan.text.toString().trim()
        val beratBadan = binding.edBeratBadan.text.toString().trim()
        val nomorTelepon = binding.edNomor.text.toString().trim()
        val nomorDarurat = binding.edNomorDarurat.text.toString().trim()
        val alamat = binding.edAlamat.text.toString().trim()

        val isFormValid = nik.isNotEmpty() && nama.isNotEmpty() && tanggalLahir.isNotEmpty() &&
                tinggiBadan.isNotEmpty() && beratBadan.isNotEmpty() &&
                nomorTelepon.isNotEmpty() && nomorDarurat.isNotEmpty() &&
                alamat.isNotEmpty() &&
                selectedGender.isNotEmpty() && selectedGender != "Pilih jenis kelamin"

        updateButtonState(isFormValid)
    }


    private fun updateButtonState(isFormValid: Boolean) {
        binding.btnDaftar.isEnabled = isFormValid
        binding.btnDaftar.setBackgroundResource(if (isFormValid) R.drawable.bg_btn else R.drawable.bg_btn_unactive)
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        val url = urlAPI.endPoint.url

        val body = JSONObject().apply {
            put("national_id", binding.edNik.text.toString().trim())
            put("email", email)
            put("password", password)
            put("password_confirm", confirmPassword)
            put("name", binding.edNama.text.toString().trim())
            put("phone", binding.edNomor.text.toString().trim())
            put("emergency_phone", binding.edNomorDarurat.text.toString().trim())
            put("date_of_birth", getTanggalLahir)
            put("weight", binding.edBeratBadan.text.toString().trim())
            put("height", binding.edTinggiBadan.text.toString().trim())
            put("address", "random street")
            put("gender", selectedGender)
            put("address", binding.edAlamat.text.toString().trim())
        }

        AndroidNetworking.post("$url/api/users/register")
            .addJSONObjectBody(body)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnDaftar.visibility = View.VISIBLE

                    // Show success toast
                    Toast.makeText(this@PersonalData, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // Log the response
                    Log.d("Register", "Response: $response")

                    // Handle the response
                    val data = response.getJSONObject("data")

                    // Navigate to MainActivity with Profile fragment
                    val intent = Intent(this@PersonalData, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("fragment", "profile")
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onError(anError: ANError) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnDaftar.visibility = View.VISIBLE

                    // Show error toast
                    Toast.makeText(this@PersonalData, "Registration failed: ${anError.message}", Toast.LENGTH_SHORT).show()

                    // Log the error details
                    Log.e("Register", "Error: ${anError.errorDetail}")
                    Log.e("Register", "Error Response: ${anError.response}")

                    // Handle the error
                    anError.printStackTrace()
                }
            })
    }
}
