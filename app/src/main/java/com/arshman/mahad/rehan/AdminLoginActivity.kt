// AdminLoginActivity.kt
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

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var dbHelper: AdminDbHelper
    private val adminRef = FirebaseDatabase.getInstance().getReference("Admin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Admin")
        dbHelper = AdminDbHelper(this)

        // Sync any offline‑cached admin registrations
        if (isConnected()) {
            syncPendingAdmins()
        }

        emailInput    = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.Password)
        loginButton   = findViewById(R.id.loginButton)
        registerButton= findViewById(R.id.Register)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass  = passwordInput.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, pass)
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterAdminActivity::class.java))
        }
    }

    private fun syncPendingAdmins() {
        val pending = dbHelper.getUnsynced()
        for (local in pending) {
            auth.createUserWithEmailAndPassword(local.email, local.password)
                .addOnSuccessListener { authRes ->
                    val uid = authRes.user?.uid ?: return@addOnSuccessListener
                    val remote = local.copy(id = uid, isSynced = 1)
                    adminRef.child(uid)
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
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminHomeActivity::class.java))
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
