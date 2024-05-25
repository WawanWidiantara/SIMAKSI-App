package com.example.simaksigunung

import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AdapterHistory(val context: Context, val dataList: List<DataHistory>) :
    RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    private val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val dateFormatOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fetch_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem, dateFormatInput, dateFormatOutput)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idOrder: TextView = itemView.findViewById(R.id.idOrder)
        val tanggalOrder: TextView = itemView.findViewById(R.id.tanggalOrder)
        val statusOrder: TextView = itemView.findViewById(R.id.statusOrder)
        val parent: RelativeLayout = itemView.findViewById(R.id.parent)

        fun bind(item: DataHistory, dateFormatInput: SimpleDateFormat, dateFormatOutput: SimpleDateFormat) {
            idOrder.text = item.id_order
            tanggalOrder.text = formatDate(item.tanggal_order, dateFormatInput, dateFormatOutput)
            statusOrder.text = item.status_order
            parent.setOnClickListener {
                val intent = Intent(itemView.context, DetailTransaksi::class.java)
                intent.putExtra("id_order", item.id_order)
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

        private fun formatDate(dateString: String, dateFormatInput: SimpleDateFormat, dateFormatOutput: SimpleDateFormat): String {
            return try {
                val date = dateFormatInput.parse(dateString)
                dateFormatOutput.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
