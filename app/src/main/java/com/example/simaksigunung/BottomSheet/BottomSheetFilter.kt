package com.example.simaksigunung.BottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListenerFilter
import com.example.simaksigunung.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.ClassCastException

class BottomSheetFilter(private var listener : BottomSheetListenerFilter) : BottomSheetDialogFragment() {
    private lateinit var btnTerbaru : Button
    private lateinit var btnTerlama : Button
    private lateinit var btnMenunggu : Button
    private lateinit var btnLunas : Button
    private lateinit var btnAktif : Button
    private lateinit var btnSelesai : Button
    private lateinit var btnDibatalkan : Button
    var bottomSheetListener: BottomSheetListenerFilter? = null
    var waktu : String = ""
    var status : String = ""
    init {
        this.bottomSheetListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter_history, container, false)

        btnTerbaru = view.findViewById<Button>(R.id.btnTerbaru)
        btnTerlama = view.findViewById<Button>(R.id.btnTerlama)
        btnMenunggu = view.findViewById<Button>(R.id.btnMenunggu)
        btnLunas = view.findViewById<Button>(R.id.btnLunas)
        btnAktif = view.findViewById<Button>(R.id.btnAktif)
        btnSelesai = view.findViewById<Button>(R.id.btnSelesai)
        btnDibatalkan = view.findViewById<Button>(R.id.btnDibatalkan)

        btnTerbaru.setOnClickListener {
            updateButtonStyle(btnTerbaru, listOf(btnTerlama))
            waktu = "terbaru"
            sendFilterData()
        }
        btnTerlama.setOnClickListener {
            updateButtonStyle(btnTerlama, listOf(btnTerbaru))
            waktu = "terlama"
            sendFilterData()
        }

        btnMenunggu.setOnClickListener {
            updateButtonStyle(btnMenunggu, listOf(btnLunas, btnAktif, btnSelesai, btnDibatalkan))
            status = "menunggu"
            sendFilterData()
        }

        btnLunas.setOnClickListener {
            updateButtonStyle(btnLunas, listOf(btnMenunggu, btnAktif, btnSelesai, btnDibatalkan))
            status = "lunas"
            sendFilterData()
        }

        btnAktif.setOnClickListener {
            updateButtonStyle(btnAktif, listOf(btnMenunggu, btnLunas, btnSelesai, btnDibatalkan))
            status = "aktif"
            sendFilterData()
        }

        btnSelesai.setOnClickListener {
            updateButtonStyle(btnSelesai, listOf(btnMenunggu, btnLunas, btnAktif, btnDibatalkan))
            status = "selesai"
            sendFilterData()
        }

        btnDibatalkan.setOnClickListener {
            updateButtonStyle(btnDibatalkan, listOf(btnMenunggu, btnLunas, btnAktif, btnSelesai))
            status = "dibatalkan"
            sendFilterData()
        }

        return view
    }

    private fun updateButtonStyle(activeButton: Button, inactiveButtons: List<Button>) {
        activeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        activeButton.setBackgroundResource(R.drawable.bg_filter_active)
        activeButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

        inactiveButtons.forEach { button ->
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            button.setBackgroundResource(R.drawable.bg_filter_unactive)
            button.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)
        }
    }

    private fun sendFilterData() {
        if (status.isNotEmpty() && waktu.isNotEmpty()) {
            listener.onFilterApplied(status, waktu)
            dismiss()
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
        dialog?.window?.setDimAmount(0.5f) // Set transparansi latar belakang
    }

    override fun onStop() {
        super.onStop()
        dismiss() // Pastikan dismiss dipanggil untuk menutup BottomSheetDialogFragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            bottomSheetListener = context as BottomSheetListenerFilter?
        }catch (e : ClassCastException){

        }
    }
}
