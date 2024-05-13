package com.example.simaksigunung

import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class AdapterHistory(val context: Context, val dataList: List<DataHistory>) :
    RecyclerView.Adapter<AdapterHistory.ViewHolder>() {


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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idOrder: TextView = itemView.findViewById(R.id.idOrder)
        val tanggalOrder: TextView = itemView.findViewById(R.id.tanggalOrder)
        val statusOrder: TextView = itemView.findViewById(R.id.statusOrder)
        val parent: RelativeLayout = itemView.findViewById(R.id.parent)
        fun bind(item: DataHistory) {
            idOrder.text = item.id_order
            tanggalOrder.text = item.tanggal_order
            statusOrder.text = item.status_order
            parent.setOnClickListener {
                val intent = Intent(itemView.context, DetailTransaksi::class.java)
                intent.putExtra("id_order", item.id_order)
                itemView.context.startActivity(intent)
            }
            if (item.status_order.equals("menunggu")) {
                statusOrder.text = "Menunggu"
                parent.setBackgroundResource(R.drawable.bg_fetch_history_belum_lunas)
            } else if (item.status_order.equals("lunas")) {
                parent.setBackgroundResource(R.drawable.bg_fetch_history_menunggu)
                statusOrder.text = "Lunas"
            } else if (item.status_order.equals("aktif")) {
                parent.setBackgroundResource(R.drawable.bg_fetch_history_diproses)
                statusOrder.text = "Aktif"
            } else if (item.status_order.equals("selesai")) {
                parent.setBackgroundResource(R.drawable.bg_fetch_history_selesai)
                statusOrder.text = "Selesai"
            } else if (item.status_order.equals("dibatalkan")) {
                parent.setBackgroundResource(R.drawable.bg_fetch_history_dibatalkan)
                statusOrder.text = "Dibatalkan"
            }
        }
    }
}