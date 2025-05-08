package com.arshman.mahad.rehan

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BookingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var dbHelper: BookingDbHelper
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // 1) Initialize local DB helper & Firebase reference
        dbHelper = BookingDbHelper(this)
        database = FirebaseDatabase.getInstance().getReference("Booking")

        // 2) RecyclerView setup
        val recycler = findViewById<RecyclerView>(R.id.rvStatusSummary)
        adapter = BookingAdapter(bookingList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // 3) Sync offline data & load bookings
        if (isConnected()) {
            syncPendingBookings()
            loadFromFirebase()
        } else {
            loadFromCache()
        }

        // 4) “Create Booking” button
        val bookingBtn = findViewById<Button>(R.id.btnCreateBooking)
        bookingBtn.setOnClickListener {
            startActivity(Intent(this, CreateBookingActivity::class.java))
        }
    }

    /** Upload any locally‑saved bookings that haven’t been synced yet */
    private fun syncPendingBookings() {
        val pending = dbHelper.getUnsynced()
        for (b in pending) {
            database.child(b.id)
                .setValue(b)
                .addOnSuccessListener { dbHelper.markSynced(b.id) }
        }
    }

    /** Load live data from Firebase, cache locally, and update UI */
    private fun loadFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                for (child in snapshot.children) {
                    val b = child.getValue(Booking::class.java) ?: continue
                    bookingList.add(b)
                    b.isSynced = 1
                    dbHelper.insertOrUpdate(b)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@BookingActivity,
                    "Failed to load bookings: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /** Fallback: display any bookings still unsynced (i.e., created offline) */
    private fun loadFromCache() {
        bookingList.clear()
        bookingList.addAll(dbHelper.getUnsynced())
        adapter.notifyDataSetChanged()
    }

    /** Simple network check */
    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
