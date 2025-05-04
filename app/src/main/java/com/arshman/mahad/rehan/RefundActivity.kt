package com.arshman.mahad.rehan


import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RefundActivity : AppCompatActivity() {

    // Re-use the same data model from Payments screen
    private val history = mutableListOf<PaymentRecord>()
    private lateinit var adapter: PreviousPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)

        // find views
        val rvPrevious = findViewById<RecyclerView>(R.id.rvPreviousPayments)


        // setup RecyclerView
        adapter = PreviousPaymentAdapter(history)
        rvPrevious.layoutManager = LinearLayoutManager(this)
        rvPrevious.adapter       = adapter

        // dummy data until your DB is ready
        listOf(
            PaymentRecord("27 September 2024","Regular","$55"),
            PaymentRecord("24 September 2024","Regular","$55")
        ).forEach { history.add(it) }
        adapter.notifyDataSetChanged()

    }
}
