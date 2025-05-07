package com.arshman.mahad.rehan

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.onesignal.OneSignal
import java.text.SimpleDateFormat
import java.util.*

class CreateBookingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var notificationService: NotificationService
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private val TAG = "CreateBookingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_booking)

        database = FirebaseDatabase.getInstance().getReference("Booking")
        notificationService = NotificationService(this)
        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val titleEt = findViewById<EditText>(R.id.etBookingTitle)
        val descEt = findViewById<EditText>(R.id.etBookingDesc)
        val spinner = findViewById<Spinner>(R.id.spinnerTimes)
        val submit = findViewById<Button>(R.id.btnSubmitBooking)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener { finish() }

        val times = listOf("Select Time", "09:00 AM", "11:00 AM", "02:00 PM", "04:00 PM")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times)

        submit.setOnClickListener {
            val title = titleEt.text.toString().trim()
            val description = descEt.text.toString().trim()
            val time = spinner.selectedItem as String

            if (title.isEmpty()) {
                titleEt.error = "Title is required"
                return@setOnClickListener
            }
            if (time == "Select Time") {
                Toast.makeText(this, "Please select a valid time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            submit.isEnabled = false

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val bookingId = database.push().key ?: return@setOnClickListener
            val booking = Booking(
                id = bookingId,
                title = title,
                description = description,
                time = time,
                date = currentDate,
                userId = currentUser.uid
            )

            database.child(bookingId).setValue(booking)
                .addOnSuccessListener {
                    val deviceState = OneSignal.getDeviceState()
                    if (deviceState == null || deviceState.userId.isNullOrEmpty()) {
                        Log.e(TAG, "OneSignal device state or player ID is null")
                        showErrorAndFinish("Unable to send notification - device not registered")
                        return@addOnSuccessListener
                    }

                    val playerId = deviceState.userId
                    val data = mapOf(
                        "bookingId" to bookingId,
                        "bookingTitle" to title,
                        "bookingTime" to time,
                        "bookingDate" to currentDate,
                        "notificationType" to "booking_confirmation"
                    )

                    notificationService.sendBookingNotification(
                        playerId = playerId,
                        heading = "Booking Confirmation",
                        message = "Your booking for \"$title\" at $time has been confirmed!",
                        data = data
                    )

                    progressBar.visibility = View.GONE
                    submit.isEnabled = true

                    Toast.makeText(this, "Booking created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    submit.isEnabled = true
                    Log.e(TAG, "Failed to create booking", e)
                    Toast.makeText(this, "Failed to create booking: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showErrorAndFinish(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}