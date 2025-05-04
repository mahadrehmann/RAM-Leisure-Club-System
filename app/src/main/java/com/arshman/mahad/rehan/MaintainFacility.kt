package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

class MaintainFacility : AppCompatActivity() {
    private lateinit var binding: MaintainFacility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maintain_facility)

        val schedule = findViewById<Button>(R.id.btnSchedule)

        schedule.setOnClickListener {
            Toast.makeText(this, "Schedule button clicked", Toast.LENGTH_SHORT).show()
        }

    }
}
