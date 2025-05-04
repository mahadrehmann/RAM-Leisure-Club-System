package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MemberHomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_home_page)

        val booking = findViewById<LinearLayout>(R.id.cardBook)
        val bills = findViewById<LinearLayout>(R.id.cardBills)
        val membership = findViewById<LinearLayout>(R.id.cardMembership)
        val profile = findViewById<LinearLayout>(R.id.cardProfileSettings)
        val logout = findViewById<Button>(R.id.btnLogout)
        val profileview = findViewById<LinearLayout>(R.id.llProfile)
        booking.setOnClickListener {
            // Handle booking button click
            startActivity(Intent(this, BookingActivity::class.java))
        }
        bills.setOnClickListener {
            // Handle bills button click
            startActivity(Intent(this, PaymentActivity::class.java))
        }
        membership.setOnClickListener {
            // Handle membership button click
            startActivity(Intent(this, MembershipActivity::class.java))
        }
        profile.setOnClickListener {
            // Handle profile button click
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        profileview.setOnClickListener {
            // Handle profile button click
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}