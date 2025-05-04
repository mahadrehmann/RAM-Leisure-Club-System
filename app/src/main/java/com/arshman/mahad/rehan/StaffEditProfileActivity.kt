package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StaffEditProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_edit_profile)

        val name = findViewById<EditText>(R.id.nameEditText)
        val username= findViewById<EditText>(R.id.usernameEditText)
        val contact = findViewById<EditText>(R.id.contactEditText)

        val done = findViewById<Button>(R.id.myBtn)
        done.setOnClickListener {
            startActivity(Intent(this, MemberHomePage::class.java))

        }

    }
}