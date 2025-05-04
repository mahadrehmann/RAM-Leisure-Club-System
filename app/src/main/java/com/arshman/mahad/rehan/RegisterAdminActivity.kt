package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RegisterAdminActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_admin)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Admin")

        username = findViewById(R.id.Username)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        phoneNumber = findViewById(R.id.PhoneNumber)
        registerButton = findViewById(R.id.Register)

        registerButton.setOnClickListener {
            saveUserData()
        }

        val loginButton = findViewById<Button>(R.id.Login)
        loginButton.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
    }

    private fun saveUserData() {
        val userUsername = username.text.toString().trim()
        val userEmail = email.text.toString().trim()
        val userPassword = password.text.toString().trim()
        val userPhoneNumber = phoneNumber.text.toString().trim()

        if (userUsername.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userPhoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        checkIfUserExists(userUsername, userEmail) { exists, field ->
            if (exists) {
                when (field) {
                    "username" -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    "email" -> Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                }
            } else {
                registerUser(userUsername, userEmail, userPassword, userPhoneNumber)
            }
        }
    }

    private fun checkIfUserExists(username: String, email: String, callback: (Boolean, String?) -> Unit) {
        database.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        callback(true, "username")
                    } else {
                        database.orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        callback(true, "email")
                                    } else {
                                        callback(false, null)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@RegisterAdminActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RegisterAdminActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registerUser(username: String, email: String, password: String, phoneNumber: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val staff = Staff(
                            id = userId,
                            username = username,
                            email = email,
                            password = password,
                            phone = phoneNumber,
                            name = "",
                            dp = ""

                        )

                        database.child(userId).setValue(staff)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AdminEditProfileActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to register staff", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}