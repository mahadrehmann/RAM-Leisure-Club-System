package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: PaymentAdapter
    private val paymentList = mutableListOf<Payment>()
    private var selectedPayment: Payment? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val recycler = findViewById<RecyclerView>(R.id.rvPrevious)
        val btnPaySelected = findViewById<Button>(R.id.btnPaySelected)
        database = FirebaseDatabase.getInstance().getReference("Payment")

        // Set up RecyclerView
        adapter = PaymentAdapter(paymentList) { payment ->
            selectedPayment = payment
            Toast.makeText(this, "Selected Payment: ${payment.type}", Toast.LENGTH_SHORT).show()
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Load payments from Firebase
        loadPayments()

        // Handle "Pay Selected" button click
        btnPaySelected.setOnClickListener {
            selectedPayment?.let { payment ->
                markAsPaid(payment)
            } ?: Toast.makeText(this, "Please select a payment to pay", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPayments() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paymentList.clear()
                for (child in snapshot.children) {
                    val payment = child.getValue(Payment::class.java)
                    payment?.let { paymentList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaymentActivity, "Failed to load payments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markAsPaid(payment: Payment) {
        database.child(payment.id).removeValue()
            .addOnSuccessListener {
                paymentList.remove(payment)
                adapter.notifyDataSetChanged()
                selectedPayment = null
                Toast.makeText(this, "Payment marked as paid", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to mark payment as paid: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}