package com.example.simaksigunung

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MembersAdapter(private val members: MutableList<Member>, private val onDelete: (Int) -> Unit) :
    RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nikUser: TextView = view.findViewById(R.id.nikUser)
        val nameUser: TextView = view.findViewById(R.id.nameUser)
        val deleteUser: TextView = view.findViewById(R.id.deleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fetch_add_personel, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        holder.nikUser.text = member.nik.toString()  // Assuming 'id' represents NIK here
        holder.nameUser.text = member.name
        holder.deleteUser.setOnClickListener {
            onDelete(member.id)
        }
    }

    override fun getItemCount(): Int = members.size

    fun updateMembers(newMembers: List<Member>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }
}
