package com.example.simaksigunung.booking

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.simaksigunung.BottomSheet.BottomSheetAddMember
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListener
import com.example.simaksigunung.R
import com.example.simaksigunung.api.urlAPI
import com.example.simaksigunung.databinding.ActivityPesanJalurBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PesanJalur : AppCompatActivity(), BottomSheetListener {
    private lateinit var binding: ActivityPesanJalurBinding
    private var bottomSheetDialog: BottomSheetAddMember? = null
    private var memberAdapter: MembersAdapter? = null
    private val members = mutableListOf<Member>()
    var getTanggalNaik = ""
    var getTanggalTurun = ""
    private var tripId: Int = -1  // Replace with the actual trip ID
    private val dateFormatDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormatServer = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanJalurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidNetworking.initialize(this)

        // Retrieve the tripId from the intent
        tripId = intent.getIntExtra("tripId", -1)

        binding.edTanggalNaik.setOnClickListener {
            showDatePicker(this) { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time

                val formattedDateEd = dateFormatDisplay.format(selectedDate)
                binding.edTanggalNaik.setText(formattedDateEd)
                getTanggalNaik = dateFormatServer.format(selectedDate)
                validateForm()
            }
        }

        binding.edTanggalTurun.setOnClickListener {
            if (getTanggalNaik.isEmpty()) {
                Toast.makeText(this, "Please select Tanggal Naik first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendarNaik = Calendar.getInstance().apply {
                time = dateFormatServer.parse(getTanggalNaik)!!
            }

            showDatePicker(this) { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time

                val formattedDateEd = dateFormatDisplay.format(selectedDate)
                binding.edTanggalTurun.setText(formattedDateEd)
                getTanggalTurun = dateFormatServer.format(selectedDate)
                validateForm()
            }
        }


        binding.spinnerMetodePembayaran.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    validateForm()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    binding.spinnerMetodePembayaran.setBackgroundResource(R.drawable.bg_ed_unclicked)
                }
            }

        binding.btnAddPersonel.setOnClickListener {
            dialogBottomSheet()
        }

        binding.btnPesan.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnPesan.visibility = View.GONE
            postTripConfirmation()
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        memberAdapter = MembersAdapter(members) { memberId ->
            // Handle delete member
            deleteMember(memberId)
        }
        binding.recyclerView.adapter = memberAdapter

        // Fetch members
        fetchMembers(tripId)
    }

    private fun showDatePicker(context: Context, listener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        // Calculate the date one month from today
        val oneMonthFromToday = Calendar.getInstance().apply {
            add(Calendar.MONTH, 1)
        }

        // Initialize DatePickerDialog with today's date
        val datePickerDialog = DatePickerDialog(
            context, listener,
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict selectable dates to one month from today
        datePickerDialog.datePicker.minDate = today.timeInMillis
        datePickerDialog.datePicker.maxDate = oneMonthFromToday.timeInMillis
        datePickerDialog.show()
    }


    private fun dialogBottomSheet() {
        bottomSheetDialog = BottomSheetAddMember()
        bottomSheetDialog!!.setBottomSheetListener(this)
        bottomSheetDialog!!.show(supportFragmentManager, bottomSheetDialog!!.tag)
        bottomSheetDialog!!.dialog?.setOnShowListener {
            val dialog = bottomSheetDialog!!.dialog as BottomSheetDialog
            val container =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.parent as View
            container.setBackgroundColor(Color.parseColor("#80000000")) // Warna hitam transparan dengan alpha 128
        }
    }

    private fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
    }

    override fun onMemberAdded(memberId: Int) {
        addMember(memberId)
    }

    private fun addMember(memberId: Int) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        val body = JSONObject().apply {
            put("user_id", memberId)
        }

        AndroidNetworking.post("$url/api/members/")
            .addJSONObjectBody(body)
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Toast.makeText(
                        this@PesanJalur,
                        "Member added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissBottomSheet()
                    fetchMembers(tripId)
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        this@PesanJalur,
                        "No Member Detected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun fetchMembers(tripId: Int) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url

        AndroidNetworking.get("$url/api/members?trip_id=$tripId")
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        val dataArray = it.getJSONArray("data")
                        members.clear()  // Clear the existing list
                        for (i in 0 until dataArray.length()) {
                            val memberObject = dataArray.getJSONObject(i)
                            val userObject = memberObject.getJSONObject("user")
                            val member = Member(
                                id = memberObject.getInt("id"),
                                name = userObject.getString("name"),
                                nik = userObject.getString("id")  // Use member id as nik
                            )
                            members.add(member)
                        }
                        Log.d("PesanJalur", "Fetched members: $members")
                        memberAdapter?.notifyDataSetChanged()
                        validateForm()
                    }
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        this@PesanJalur,
                        "Failed to fetch members: ${anError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("PesanJalur", "Error fetching members: ${anError.errorDetail}")
                }
            })
    }

    private fun deleteMember(memberId: Int) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        Log.d("PesanJalur", "Deleting member with ID: $memberId")
        Log.d("PesanJalur", "Authorization: $userId")

        AndroidNetworking.delete("$url/api/members/$memberId")
            .addHeaders("Authorization", userId)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Toast.makeText(this@PesanJalur, "Member deleted successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("PesanJalur", "Delete response: $response")
                    fetchMembers(tripId)
                }

                override fun onError(anError: ANError) {
                    val errorBody = anError.errorBody
                    val errorMessage = anError.message
                    val errorCode = anError.errorCode
                    Log.e("PesanJalur", "Error deleting member: $errorMessage")
                    Log.e("PesanJalur", "Response code: $errorCode")
                    Log.e("PesanJalur", "Error response: $errorBody")

                    if (errorCode == 404) {
                        Toast.makeText(this@PesanJalur, "Member not found. It might have already been deleted.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PesanJalur, "Failed to delete member: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun validateForm() {
        val isTanggalNaikValid = getTanggalNaik.isNotEmpty()
        val isTanggalTurunValid = getTanggalTurun.isNotEmpty()
        val isMetodePembayaranValid = binding.spinnerMetodePembayaran.selectedItemPosition != AdapterView.INVALID_POSITION
        val isMemberListValid = members.isNotEmpty()

        val isFormValid = isTanggalNaikValid && isTanggalTurunValid && isMetodePembayaranValid && isMemberListValid

        binding.btnPesan.isEnabled = isFormValid
        binding.btnPesan.setBackgroundResource(if (isFormValid) R.drawable.bg_btn else R.drawable.bg_btn_unactive)
    }

    private fun postTripConfirmation() {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)?.toString() ?: ""

        if (userId == "-1") {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val url = urlAPI.endPoint.url
        val body = JSONObject().apply {
            put("start_date", getTanggalNaik)
            put("end_date", getTanggalTurun)
            put("bank", binding.spinnerMetodePembayaran.selectedItem.toString().toLowerCase(Locale.ROOT))
        }

        AndroidNetworking.post("$url/api/trips/confirm-create")
            .addJSONObjectBody(body)
            .addHeaders("Authorization", userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Toast.makeText(this@PesanJalur, "Trip confirmed successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@PesanJalur, PesanJalurSukses::class.java))
                    binding.progressBar.visibility = View.GONE
                    binding.btnPesan.visibility = View.VISIBLE
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(this@PesanJalur, "Failed to confirm trip: ${anError.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnPesan.visibility = View.VISIBLE
                }
            })
    }
}
