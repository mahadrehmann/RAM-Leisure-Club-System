package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_login)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Admin")

        // Initialize views
        emailInput = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.Password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.Register)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateStaff(email, password)
            }
        }

        // Handle registration button click
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun authenticateStaff(email: String, password: String) {
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (staffSnapshot in snapshot.children) {
                            val staff = staffSnapshot.getValue(Staff::class.java)
                            if (staff != null && staff.password == password) {
                                Toast.makeText(this@AdminLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AdminLoginActivity, AdminHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                                return
                            }
                        }
                        Toast.makeText(this@AdminLoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AdminLoginActivity, "Staff not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminLoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}