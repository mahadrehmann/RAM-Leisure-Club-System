package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Booking(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = ""
)

class CreateBookingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_booking)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Booking")

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val titleEt = findViewById<EditText>(R.id.etBookingTitle)
        val descEt = findViewById<EditText>(R.id.etBookingDesc)
        val spinner = findViewById<Spinner>(R.id.spinnerTimes)
        val submit = findViewById<Button>(R.id.btnSubmitBooking)

        // Dummy times
        val times = listOf("Select Time", "09:00 AM", "11:00 AM", "02:00 PM", "04:00 PM")
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )

        submit.setOnClickListener {
            val title = titleEt.text.toString().trim()
            val description = descEt.text.toString().trim()
            val time = spinner.selectedItem as String

            // Validate input
            if (title.isEmpty()) {
                titleEt.error = "Title is required"
                return@setOnClickListener
            }
            if (time == "Select Time") {
                Toast.makeText(this, "Please select a valid time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate a unique ID for the booking
            val bookingId = database.push().key ?: return@setOnClickListener

            // Create a Booking object
            val booking = Booking(
                id = bookingId,
                title = title,
                description = description,
                time = time
            )

            // Save booking to Firebase
            database.child(bookingId).setValue(booking)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create booking: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}