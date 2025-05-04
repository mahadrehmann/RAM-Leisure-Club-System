package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StaffLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Staff")

        emailInput = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.Password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.Register)
        val stafflogin = findViewById<Button>(R.id.StaffLogin)


//        // Check if user is already logged in
//        if (auth.currentUser != null) {
//            val userId = auth.currentUser?.uid
//            if (userId != null) {
//                val userOnlineRef = database.child(userId).child("isOnline")
//                userOnlineRef.setValue(true) // Set user as online
//                userOnlineRef.onDisconnect().setValue(false) // Set to offline when disconnected
//            }
//
//            // Redirect to MemberHomePage
//            val intent = Intent(this, MemberHomePage::class.java)
//            startActivity(intent)
//            finish()
//        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, password)
            }
        }

        // Handle registration button click
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterStaffActivity::class.java)
            startActivity(intent)
        }
    }

    private fun authenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Set user as online and set up onDisconnect
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userOnlineRef = database.child(userId).child("isOnline")
                        userOnlineRef.setValue(true) // Set user as online
                        userOnlineRef.onDisconnect().setValue(false) // Set to offline when disconnected
                    }

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StaffHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}