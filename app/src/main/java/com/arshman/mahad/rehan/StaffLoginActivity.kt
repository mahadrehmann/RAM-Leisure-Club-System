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

class StaffLoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_login)

        val email = findViewById<EditText>(R.id.Email)
        val password = findViewById<EditText>(R.id.Password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val register= findViewById<Button>(R.id.Register)

        loginButton.setOnClickListener {
            startActivity(Intent(this, StaffHomeActivity::class.java))
        }

        register.setOnClickListener {
            startActivity(Intent(this, RegisterStaffActivity::class.java))
        }


    }
}