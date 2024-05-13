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
            btnTerbaru.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnTerbaru.setBackgroundResource(R.drawable.bg_filter_active)
            btnTerbaru.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnTerlama.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnTerlama.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnTerlama.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            waktu = "terbaru"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }
        btnTerlama.setOnClickListener {
            btnTerlama.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnTerlama.setBackgroundResource(R.drawable.bg_filter_active)
            btnTerlama.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnTerbaru.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnTerbaru.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnTerbaru.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            waktu = "terlama"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }

        btnMenunggu.setOnClickListener {
            btnMenunggu.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnMenunggu.setBackgroundResource(R.drawable.bg_filter_active)
            btnMenunggu.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnLunas.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnLunas.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnLunas.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnAktif.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnAktif.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnSelesai.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnSelesai.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnSelesai.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnDibatalkan.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnDibatalkan.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnDibatalkan.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            status = "menunggu"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }

        btnLunas.setOnClickListener {
            btnLunas.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnLunas.setBackgroundResource(R.drawable.bg_filter_active)
            btnLunas.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnMenunggu.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnMenunggu.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnMenunggu.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnAktif.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnAktif.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnSelesai.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnSelesai.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnSelesai.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnDibatalkan.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnDibatalkan.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnDibatalkan.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            status = "lunas"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }

        btnAktif.setOnClickListener {
            btnAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnAktif.setBackgroundResource(R.drawable.bg_filter_active)
            btnAktif.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnMenunggu.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnMenunggu.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnMenunggu.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnLunas.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnLunas.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnLunas.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnSelesai.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnSelesai.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnSelesai.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnDibatalkan.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnDibatalkan.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnDibatalkan.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            status = "aktif"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }

        btnSelesai.setOnClickListener {
            btnSelesai.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnSelesai.setBackgroundResource(R.drawable.bg_filter_active)
            btnSelesai.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnMenunggu.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnMenunggu.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnMenunggu.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnLunas.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnLunas.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnLunas.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnAktif.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnAktif.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnDibatalkan.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnDibatalkan.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnDibatalkan.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            status = "selesai"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }

        btnDibatalkan.setOnClickListener {
            btnDibatalkan.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnDibatalkan.setBackgroundResource(R.drawable.bg_filter_active)
            btnDibatalkan.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)

            btnAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnAktif.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnAktif.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnMenunggu.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnMenunggu.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnMenunggu.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnLunas.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnLunas.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnLunas.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            btnSelesai.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selesai))
            btnSelesai.setBackgroundResource(R.drawable.bg_filter_unactive)
            btnSelesai.typeface = ResourcesCompat.getFont(requireContext(), R.font.lato_medium)

            status = "dibatalkan"
//            if (status.isNotEmpty() && waktu.isNotEmpty()){
//                sendDataToActivity(waktu, status)
//            }
        }


        return view
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
//    private fun sendDataToActivity(waktu : String, status : String) {
//        listener?.onDataReceived(waktu, status)
//    }

}