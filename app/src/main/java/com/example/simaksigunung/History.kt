package com.example.simaksigunung

import android.graphics.Color
import android.os.Bundle
import android.view.FrameStats
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simaksigunung.BottomSheet.BottomSheetFilter
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListenerFilter
import com.example.simaksigunung.databinding.FragmentHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class History : Fragment(), BottomSheetListenerFilter {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: AdapterHistory
    private var dataList: List<DataHistory> = emptyList()
    private  var bottomSheetDialog : BottomSheetFilter? = null

    var idData = listOf(
        "ID: 321-ini-random",
        "ID: 321-ini-random",
        "ID: 321-ini-random",
        "ID: 321-ini-random",
        "ID: 321-ini-random"
    )
    var tanggalData = listOf(
        "2021-08-01",
        "2021-08-02",
        "2021-08-03",
        "2021-08-04",
        "2021-08-05"
    )
    var statusData = listOf(
        "menunggu",
        "lunas",
        "aktif",
        "selesai",
        "dibatalkan"
    )

//    var dataList = listOf(
//        DataHistory(idData[0], tanggalData[0], statusData[0]),
//        DataHistory(idData[1], tanggalData[1], statusData[1]),
//        DataHistory(idData[2], tanggalData[2], statusData[2]),
//        DataHistory(idData[3], tanggalData[3], statusData[3]),
//        DataHistory(idData[4], tanggalData[4], statusData[4]),
//
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        inisiliasiRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFilter.setOnClickListener {
            dialogBottomSheet()
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            History().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun inisiliasiRecyclerView() {
        for (i in idData.indices) {
            dataList += DataHistory(idData[i], tanggalData[i], statusData[i])
        }
        binding.recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager
        adapter = AdapterHistory(requireContext(), dataList)
        binding.recyclerView.adapter = adapter
    }

    private fun dialogBottomSheet() {
        bottomSheetDialog = BottomSheetFilter(this)
        val bundle = Bundle()
        bottomSheetDialog!!.arguments = bundle
        activity?.supportFragmentManager?.let { bottomSheetDialog!!.show(it, bottomSheetDialog!!.tag) }


        bottomSheetDialog!!.dialog?.setOnShowListener {
            val dialog = bottomSheetDialog!!.dialog as BottomSheetDialog

            // Set transparansi pada latar belakang di luar bottom sheet
            val container = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.parent as View
            container.setBackgroundColor(Color.parseColor("#80000000")) // Warna hitam transparan dengan alpha 128
        }

    }


}