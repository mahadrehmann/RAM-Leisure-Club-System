package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val recycler = findViewById<RecyclerView>(R.id.rvStatusSummary)
        val button   = findViewById<Button>(R.id.btnCreateBooking)

        // dummy data
        val data = listOf("To Do" to 5, "In Progress" to 2, "Done" to 1)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recycler.adapter = StatusSummaryAdapter(data)

        button.setOnClickListener {
            startActivity(Intent(this, CreateBookingActivity::class.java))
        }
    }
}
