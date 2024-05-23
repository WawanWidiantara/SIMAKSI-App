package com.example.simaksigunung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityLoginBinding
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validationEditText()

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

        binding.btnMasuk.setOnClickListener {
            if (binding.btnMasuk.isEnabled) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnMasuk.visibility = View.GONE
                login(binding.edEmail.text.toString().trim(), binding.edPassword.text.toString().trim())
            }
        }
    }

    private fun validationEditText() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = binding.edEmail.text.toString().trim()
                val password = binding.edPassword.text.toString().trim()

                val isFormValid = email.isNotEmpty() && password.isNotEmpty()
                updateButtonState(isFormValid)
            }
        }

        binding.edEmail.addTextChangedListener(textWatcher)
        binding.edPassword.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState(isFormValid: Boolean) {
        binding.btnMasuk.isEnabled = isFormValid
        binding.btnMasuk.setBackgroundResource(if (isFormValid) R.drawable.bg_btn else R.drawable.bg_btn_unactive)
    }

    private fun login(email: String, password: String) {
        val url = urlAPI.endPoint.url

        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        AndroidNetworking.post("$url/api/users/login")
            .addJSONObjectBody(body)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnMasuk.visibility = View.VISIBLE

                    // Show success toast
                    Toast.makeText(this@Login, "Login successful!", Toast.LENGTH_SHORT).show()

                    // Log the response
                    Log.d("Login", "Response: $response")

                    // Save user data to SharedPreferences
                    saveUserData(response.getJSONObject("data"))

                    // Navigate to MainActivity with Profile fragment
                    val intent = Intent(this@Login, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("fragment", "profile")
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onError(anError: ANError) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnMasuk.visibility = View.VISIBLE

                    // Show error toast
                    Toast.makeText(this@Login, "Login failed: ${anError.message}", Toast.LENGTH_SHORT).show()

                    // Log the error details
                    Log.e("Login", "Error: ${anError.errorDetail}")
                    Log.e("Login", "Error Response: ${anError.response}")

                    // Handle the error
                    anError.printStackTrace()
                }
            })
    }

    private fun saveUserData(userData: JSONObject) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userData.getInt("id"))
        editor.putString("national_id", userData.getString("national_id"))
        editor.putString("email", userData.getString("email"))
        editor.putString("name", userData.getString("name"))
        editor.putString("phone", userData.getString("phone"))
        editor.putString("emergency_phone", userData.getString("emergency_phone"))
        editor.putString("gender", userData.optString("gender"))
        editor.putString("date_of_birth", userData.getString("date_of_birth"))
        editor.putInt("weight", userData.getInt("weight"))
        editor.putInt("height", userData.getInt("height"))
        editor.putString("address", userData.getString("address"))
        editor.apply()
    }
}
