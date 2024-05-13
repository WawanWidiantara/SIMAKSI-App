package com.example.simaksigunung

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simaksigunung.databinding.FragmentHomeBinding
import com.example.simaksigunung.databinding.FragmentProfileBinding


class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(activity, Register::class.java)
            startActivity(intent)
        }
        return binding.root
    }

}