package com.example.simaksigunung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simaksigunung.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        updateUserName()
        updateDate()

        binding.btnPesanRute.setOnClickListener {
            val intent = Intent(activity, SyaratKetentuan::class.java)
            startActivity(intent)
        }

        binding.detailRute.setOnClickListener {
            val intent = Intent(activity, DetailRute::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun updateUserName() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sharedPreferences?.getString("name", "Guest")
        binding.nama.text = name
    }

    private fun updateDate() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(currentDate)
        binding.tanggal.text = formattedDate
    }
}
