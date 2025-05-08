// StaffLoginActivity.kt
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

class StaffLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var dbHelper: StaffDbHelper
    private val staffRef = FirebaseDatabase.getInstance().getReference("Staff")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Staff")
        dbHelper = StaffDbHelper(this)

        // Sync any offline‑cached staff registrations
        if (isConnected()) syncPendingStaff()

        emailInput    = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.Password)
        loginButton   = findViewById(R.id.loginButton)
        registerButton= findViewById(R.id.Register)
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

        // Login click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass  = passwordInput.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, pass)
            }
        }

        // Go to RegisterStaffActivity
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterStaffActivity::class.java))
        }
    }

    private fun syncPendingStaff() {
        val pending = dbHelper.getUnsynced()
        for (local in pending) {
            auth.createUserWithEmailAndPassword(local.email, local.password)
                .addOnSuccessListener { authRes ->
                    val uid = authRes.user?.uid ?: return@addOnSuccessListener
                    val remote = local.copy(id = uid, isSynced = 1)
                    staffRef.child(uid)
                        .setValue(remote)
                        .addOnSuccessListener { dbHelper.markSynced(local.id) }
                }
                .addOnFailureListener {
                    // leave unsynced to retry next time
                }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // mark online if you have isOnline flags
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StaffHomeActivity::class.java))
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

    private fun isConnected(): Boolean {
        val cm  = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
