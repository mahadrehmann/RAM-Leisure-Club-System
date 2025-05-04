package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.Email)
        val password = findViewById<EditText>(R.id.Password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val register= findViewById<Button>(R.id.Register)
        val stafflogin = findViewById<Button>(R.id.StaffLogin)
        val adminlogin = findViewById<Button>(R.id.AdminLogin)

        loginButton.setOnClickListener {
            startActivity(Intent(this, MemberHomePage::class.java))
        }

        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        stafflogin.setOnClickListener {
            startActivity(Intent(this, StaffLoginActivity::class.java))
        }

        adminlogin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }



    }
}