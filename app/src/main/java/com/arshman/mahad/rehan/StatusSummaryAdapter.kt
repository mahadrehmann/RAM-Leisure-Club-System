package com.arshman.mahad.rehan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatusSummaryAdapter(
    private val items: List<Pair<String, Int>>
) : RecyclerView.Adapter<StatusSummaryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvCount = view.findViewById<TextView>(R.id.tvCount)
        val tvLabel = view.findViewById<TextView>(R.id.tvLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val (label, count) = items[pos]
        holder.tvCount.text = count.toString()
        holder.tvLabel.text = label
    }

    override fun getItemCount() = items.size
}
