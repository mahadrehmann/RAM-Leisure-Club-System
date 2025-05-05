package com.arshman.mahad.rehan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MaintainFacilityAdapter(
    private val maintenanceList: List<FacilityMaintenance>,
    private val onLongClick: (FacilityMaintenance) -> Unit
) : RecyclerView.Adapter<MaintainFacilityAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFacility: TextView = itemView.findViewById(R.id.tvFacility)
        val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val maintenance = maintenanceList[position]
        holder.tvFacility.text = maintenance.facility
        holder.tvDueDate.text = maintenance.dueDate

        holder.itemView.setOnLongClickListener {
            onLongClick(maintenance)
            true
        }
    }

    override fun getItemCount(): Int = maintenanceList.size
}