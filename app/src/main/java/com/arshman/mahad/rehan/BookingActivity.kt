package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BookingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val recycler = findViewById<RecyclerView>(R.id.rvStatusSummary)
        database = FirebaseDatabase.getInstance().getReference("Booking")

        // Set up RecyclerView
        adapter = BookingAdapter(bookingList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Load bookings from Firebase
        loadBookings()
    }

    private fun loadBookings() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                for (child in snapshot.children) {
                    val booking = child.getValue(Booking::class.java)
                    booking?.let { bookingList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BookingActivity, "Failed to load bookings: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}