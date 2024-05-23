package com.example.simaksigunung

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.FragmentProfileBinding
import com.example.simaksigunung.register.Register

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        checkUserData()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(activity, Register::class.java)
            startActivity(intent)
        }
        binding.btnKeluar.setOnClickListener {
            showLogoutDialog()
        }

        return binding.root
    }

    private fun checkUserData() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)

        if (userId != null && userId != -1) {
            binding.btnLogin.visibility = View.GONE
            binding.btnRegister.visibility = View.GONE
            binding.photo.visibility = View.VISIBLE
            binding.nama.visibility = View.VISIBLE
            binding.personalDataBody.visibility = View.VISIBLE
            binding.btnKeluar.visibility = View.VISIBLE

            val email = sharedPreferences.getString("email", "")
            val name = sharedPreferences.getString("name", "")
            val dateOfBirth = sharedPreferences.getString("date_of_birth", "")
            val gender = sharedPreferences.getString("gender", "")
            val phone = sharedPreferences.getString("phone", "")

            binding.nama.text = name
            binding.email.text = email
            binding.tanggalLahir.text = dateOfBirth
            binding.jenisKelmain.text = gender
            binding.nomor.text = phone
            binding.singkatanNama.text = name?.firstOrNull()?.toString() ?: ""
        } else {
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.VISIBLE
            binding.photo.visibility = View.GONE
            binding.nama.visibility = View.GONE
            binding.personalDataBody.visibility = View.GONE
            binding.btnKeluar.visibility = View.GONE
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        btnTidak.setOnClickListener {
            alertDialog.dismiss()
        }

        btnYa.setOnClickListener {
            postLogoutApi()
            alertDialog.dismiss()
        }
    }

    private fun postLogoutApi() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        val url = urlAPI.endPoint.url  // Replace with your actual API endpoint

        AndroidNetworking.post("$url/api/users/logout")
            .addHeaders("Authorization", userId)
            .setContentType("application/json")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    // Handle the response
                    Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()
                    logoutUser()
                }

                override fun onError(anError: ANError?) {
                    // Handle the error
                    Toast.makeText(requireContext(), "Logout failed: ${anError?.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun logoutUser() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()

        // Refresh the fragment to update the UI
        checkUserData()
    }
}
