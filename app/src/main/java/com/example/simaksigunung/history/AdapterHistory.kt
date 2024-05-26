package com.example.simaksigunung.history

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simaksigunung.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AdapterHistory(val context: Context, val dataList: List<DataHistory>) :
    RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fetch_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idOrder: TextView = itemView.findViewById(R.id.idOrder)
        val tanggalOrder: TextView = itemView.findViewById(R.id.tanggalOrder)
        val statusOrder: TextView = itemView.findViewById(R.id.statusOrder)
        val parent: RelativeLayout = itemView.findViewById(R.id.parent)

        fun bind(item: DataHistory) {
            idOrder.text = item.id_order
            tanggalOrder.text = formatDate(item.tanggal_order)
            statusOrder.text = item.status_order
            parent.setOnClickListener {
                val numericId = item.id_order.replace("ID: ", "").trim()
                Log.d("AdapterHistory", "Numeric ID Order: $numericId")
                Log.d("AdapterHistory", "Status Order: ${item.status_order}")
                val intent = Intent(itemView.context, DetailTransaksi::class.java)
                intent.putExtra("id_order", numericId)
                intent.putExtra("status_order", item.status_order)
                itemView.context.startActivity(intent)
            }

            when (item.status_order) {
                "menunggu" -> {
                    statusOrder.text = "Menunggu"
                    parent.setBackgroundResource(R.drawable.bg_fetch_history_belum_lunas)
                }
                "lunas" -> {
                    parent.setBackgroundResource(R.drawable.bg_fetch_history_menunggu)
                    statusOrder.text = "Lunas"
                }
                "aktif" -> {
                    parent.setBackgroundResource(R.drawable.bg_fetch_history_diproses)
                    statusOrder.text = "Aktif"
                }
                "selesai" -> {
                    parent.setBackgroundResource(R.drawable.bg_fetch_history_selesai)
                    statusOrder.text = "Selesai"
                }
                "dibatalkan" -> {
                    parent.setBackgroundResource(R.drawable.bg_fetch_history_dibatalkan)
                    statusOrder.text = "Dibatalkan"
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val date = inputDateFormat.parse(dateString)
                outputDateFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
