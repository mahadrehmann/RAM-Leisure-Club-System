package com.arshman.mahad.rehan
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class PaymentRecord(val date: String, val type: String, val amount: String)

class PaymentActivity : AppCompatActivity() {

    private val previousPayments = mutableListOf<PaymentRecord>()
    private lateinit var adapter: PreviousPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // find activity views
        val tvPlanDates      = findViewById<TextView>(R.id.tvPlanDates)
        val tvAmount         = findViewById<TextView>(R.id.tvAmount)
        val tvPlanName       = findViewById<TextView>(R.id.tvPlanName)
        val rvPrevious       = findViewById<RecyclerView>(R.id.rvPrevious)
        val btnRequestRefund = findViewById<Button>(R.id.btnRequestRefund)


        // configure header card (dummy values)
        tvPlanDates.text = "1 Jan 2024 - 30 Dec 2024"
        tvAmount.text    = "$55"
        tvPlanName.text  = "Regular"

        // setup RecyclerView & adapter
        adapter = PreviousPaymentAdapter(previousPayments)
        rvPrevious.layoutManager = LinearLayoutManager(this)
        rvPrevious.adapter       = adapter

        // preload dummy history
        listOf(
            PaymentRecord("27 September 2024","Regular","$55"),
            PaymentRecord("24 September 2024","Regular","$55")
        ).forEach {
            previousPayments.add(it)
        }
        adapter.notifyDataSetChanged()

        // button click
        btnRequestRefund.setOnClickListener {
            Toast.makeText(this, "Request Refunds clicked", Toast.LENGTH_SHORT).show()
            // TODO: implement refund flow
            startActivity(Intent(this, RefundActivity::class.java))
        }

    }
}
