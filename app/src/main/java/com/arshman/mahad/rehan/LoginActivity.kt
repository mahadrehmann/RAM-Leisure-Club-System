package com.arshman.mahad.rehan

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var dbHelper: UserDbHelper
    private val membersRef = FirebaseDatabase.getInstance().getReference("Members")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase & local DB init
        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        dbHelper = UserDbHelper(this)

        // 1) Sync any offline‑cached registrations
        if (isConnected()) {
            syncPendingUsers()
        }

        // UI references
        emailInput    = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.Password)
        loginButton   = findViewById(R.id.loginButton)
        registerButton= findViewById(R.id.Register)

        // Staff/Admin login buttons
        findViewById<Button>(R.id.StaffLogin).setOnClickListener {
            startActivity(Intent(this, StaffLoginActivity::class.java))
        }
        findViewById<Button>(R.id.AdminLogin).setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        // If already logged in, mark online and go to home
        if (auth.currentUser != null) {
            auth.currentUser?.uid?.let { uid ->
                database.child(uid).child("isOnline").apply {
                    setValue(true)
                    onDisconnect().setValue(false)
                }
            }
            startActivity(Intent(this, MemberHomePage::class.java))
            finish()
            return
        }

        // Handle login
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass  = passwordInput.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, pass)
            }
        }

        // Go to registration
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /** Read cached users, create them in Firebase Auth & DB, then mark synced */
    private fun syncPendingUsers() {
        val pending = dbHelper.getUnsynced()
        for (localUser in pending) {
            auth.createUserWithEmailAndPassword(localUser.email, localUser.password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: return@addOnSuccessListener

                    // Build the “remote” user object with real UID
                    val remoteUser = localUser.copy(
                        id       = uid,
                        isSynced = 1
                    )

                    // Save to Realtime Database
                    membersRef.child(uid)
                        .setValue(remoteUser)
                        .addOnSuccessListener {
                            // Mark local row as synced so we don't retry
                            dbHelper.markSynced(localUser.id)
                        }
                        .addOnFailureListener {
                            // Leave unsynced for next attempt
                        }
                }
                .addOnFailureListener {
                    // If createUser fails (e.g. email already exists), you may handle that here
                }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Mark user online
                    auth.currentUser?.uid?.let { uid ->
                        database.child(uid).child("isOnline").apply {
                            setValue(true)
                            onDisconnect().setValue(false)
                        }
                    }
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MemberHomePage::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /** Simple network status check */
    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
