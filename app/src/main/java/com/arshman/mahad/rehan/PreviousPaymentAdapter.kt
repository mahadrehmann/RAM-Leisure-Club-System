package com.arshman.mahad.rehan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaymentAdapter(
    private val payments: List<Payment>,
    private val onPaymentClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.tvPaymentDate)
        val type: TextView = view.findViewById(R.id.tvPaymentType)
        val amount: TextView = view.findViewById(R.id.tvPaymentAmount)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPaymentClick(payments[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_previous_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.date.text = payment.date
        holder.type.text = "Type: ${payment.type}"
        holder.amount.text = "Total: ${payment.amount}"
    }

    override fun getItemCount(): Int = payments.size
}