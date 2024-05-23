package com.example.simaksigunung.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.simaksigunung.R
import com.example.simaksigunung.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        validationEditText()
        binding.btnDaftar.setOnClickListener {
            if (binding.edPassword.text.toString().trim() != binding.edKonfirmasiPassword.text.toString().trim()) {
//                SnackBarError().showToastWithButton(this, binding.root, "Daftar gagal", "Konfirmasi password tidak sama")
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnDaftar.visibility = View.GONE
                register()
            }
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun register() {
        binding.btnDaftar.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()
        val confirmPassword = binding.edKonfirmasiPassword.text.toString().trim()

        val intent = Intent(this, PersonalData::class.java).apply {
            putExtra("email", email)
            putExtra("password", password)
            putExtra("confirmPassword", confirmPassword)
        }
        startActivity(intent)
    }

    private fun validationEditText() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val emailText = binding.edEmail.text.toString().trim()
                val passwordText = binding.edPassword.text.toString().trim()
                val konfirmasiPasswordText = binding.edKonfirmasiPassword.text.toString().trim()

                val isEmailFilled = emailText.isNotEmpty()
                val isPasswordFilled = passwordText.isNotEmpty()
                val isKonfirmasiPasswordFilled = konfirmasiPasswordText.isNotEmpty()
                val isPasswordsMatch = passwordText == konfirmasiPasswordText

                updateBackground(binding.edEmail, isEmailFilled)
                updateBackground(binding.parentPassword, isPasswordFilled)
                updateBackground(binding.parentKonfirmasiPassword, isKonfirmasiPasswordFilled)

                val isFormValid = isEmailFilled && isPasswordFilled && isKonfirmasiPasswordFilled && isPasswordsMatch
                updateButtonState(isFormValid)
            }
        }

        binding.edEmail.addTextChangedListener(textWatcher)
        binding.edPassword.addTextChangedListener(textWatcher)
        binding.edKonfirmasiPassword.addTextChangedListener(textWatcher)
    }

    private fun updateBackground(view: View, isFilled: Boolean) {
        view.setBackgroundResource(if (isFilled) R.drawable.bg_ed_click else R.drawable.bg_ed_unclicked)
    }

    private fun updateButtonState(isFormValid: Boolean) {
        binding.btnDaftar.isEnabled = isFormValid
        binding.btnDaftar.setBackgroundResource(if (isFormValid) R.drawable.bg_btn else R.drawable.bg_btn_unactive)
    }
}
