package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StaffHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_home)

        val profileview=findViewById<LinearLayout>(R.id.llProfile)
        val book = findViewById<LinearLayout>(R.id.cardBook)
        val profile = findViewById<LinearLayout>(R.id.cardProfileSettings)
        val maintainFacility = findViewById<LinearLayout>(R.id.cardMaintainFacilities)


        profileview.setOnClickListener {
            // Handle profile button click
            startActivity(Intent(this, StaffProfileActivity::class.java))
        }

        book.setOnClickListener {
            // Handle booking button click
            startActivity(Intent(this, BookingActivity::class.java))
        }

        profile.setOnClickListener {
            // Handle profile button click
            startActivity(Intent(this, StaffEditProfileActivity::class.java))
        }

        maintainFacility.setOnClickListener {
            // Handle maintain facility button click
            startActivity(Intent(this, MaintainFacility::class.java))
        }
    }
}