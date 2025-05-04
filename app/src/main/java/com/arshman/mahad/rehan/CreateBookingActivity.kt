package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateBookingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_booking)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val titleEt  = findViewById<EditText>(R.id.etBookingTitle)
        val descEt   = findViewById<EditText>(R.id.etBookingDesc)
        val spinner  = findViewById<Spinner>(R.id.spinnerTimes)
        val submit   = findViewById<Button>(R.id.btnSubmitBooking)

        // dummy times
        val times = listOf("Select Time", "09:00 AM", "11:00 AM", "02:00 PM", "04:00 PM")
        spinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, times)

        submit.setOnClickListener {
            val title = titleEt.text.toString().trim()
            if (title.isEmpty()) {
                titleEt.error = "Required"
                return@setOnClickListener
            }
            val time = spinner.selectedItem as String
            Toast.makeText(this,
                "Booked “$title” at $time",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
