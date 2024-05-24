package com.example.simaksigunung.BottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListener
import com.example.simaksigunung.R
import com.example.simaksigunung.databinding.BottomSheetAddPersonelBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAddMember : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddPersonelBinding? = null
    private val binding get() = _binding!!
    private var listener: BottomSheetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetAddPersonelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edIdAnggota.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                if (input.isNotEmpty()) {
                    binding.btnAddMember.isEnabled = true
                    binding.btnAddMember.setBackgroundResource(R.drawable.bg_btn)
                } else {
                    binding.btnAddMember.isEnabled = false
                    binding.btnAddMember.setBackgroundResource(R.drawable.bg_btn_unactive)
                }
            }
        })

        binding.btnAddMember.setOnClickListener {
            val memberId = binding.edIdAnggota.text.toString().toIntOrNull()
            if (memberId != null) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnAddMember.visibility = View.GONE
                listener?.onMemberAdded(memberId)
                binding.progressBar.visibility = View.GONE
                binding.btnAddMember.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please enter a valid member ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the parent activity implements the listener
        if (context is BottomSheetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setWindowAnimations(R.style.BottomSheetAnimationExit)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.BottomSheetAnimationEnter)
        dialog?.window?.setDimAmount(0.5f) // Set transparency for the background
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
