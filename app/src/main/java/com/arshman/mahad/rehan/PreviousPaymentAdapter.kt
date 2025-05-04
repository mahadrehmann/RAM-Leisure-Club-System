package com.arshman.mahad.rehan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PreviousPaymentAdapter(
    private val items: List<PaymentRecord>
) : RecyclerView.Adapter<PreviousPaymentAdapter.PVH>() {

    inner class PVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate   : TextView = view.findViewById(R.id.tvPaymentDate)
        val tvType   : TextView = view.findViewById(R.id.tvPaymentType)
        val tvAmount : TextView = view.findViewById(R.id.tvPaymentAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_previous_payment, parent, false)
        return PVH(v)
    }

    override fun onBindViewHolder(holder: PVH, position: Int) {
        val rec = items[position]
        holder.tvDate.text   = rec.date
        holder.tvType.text   = "Type: ${rec.type}"
        holder.tvAmount.text = "Total: ${rec.amount}"
    }

    override fun getItemCount(): Int = items.size
}
