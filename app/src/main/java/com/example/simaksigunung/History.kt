package com.example.simaksigunung

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.simaksigunung.BottomSheet.BottomSheetFilter
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListenerFilter
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.FragmentHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class History : Fragment(), BottomSheetListenerFilter {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: AdapterHistory
    private var dataList: MutableList<DataHistory> = mutableListOf()
    private var bottomSheetDialog: BottomSheetFilter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        initializeRecyclerView()
        fetchHistoryData()
        setupSwipeToRefresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFilter.setOnClickListener {
            dialogBottomSheet()
        }
    }


    private fun initializeRecyclerView() {
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
        activity?.supportFragmentManager?.let {
            bottomSheetDialog!!.show(
                it,
                bottomSheetDialog!!.tag
            )
        }

        bottomSheetDialog!!.dialog?.setOnShowListener {
            val dialog = bottomSheetDialog!!.dialog as BottomSheetDialog

            // Set transparency for the background outside the bottom sheet
            val container =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.parent as View
            container.setBackgroundColor(Color.parseColor("#80000000")) // Semi-transparent black color with alpha 128
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchHistoryData()
        }
    }

    private fun fetchHistoryData() {
        val sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1).toString()

        if (userId == "-1") {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            binding.swipeRefreshLayout.isRefreshing = false
            return
        }

        val url = urlAPI.endPoint.url

        AndroidNetworking.get("$url/api/trips?status=&order=terlama")
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        val dataArray = it.getJSONArray("data")
                        dataList.clear()  // Clear the existing list
                        for (i in 0 until dataArray.length()) {
                            val tripObject = dataArray.getJSONObject(i)
                            val dataHistory = DataHistory(
                                id_order = "ID: ${tripObject.getInt("id")}",
                                tanggal_order = tripObject.getString("updated_at"),
                                status_order = tripObject.getString("status")
                            )
                            dataList.add(dataHistory)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch history: ${anError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            })
    }
}
