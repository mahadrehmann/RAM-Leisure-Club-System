// RegisterStaffActivity.kt
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
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterStaffActivity : AppCompatActivity() {

    private lateinit var usernameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var registerBtn: Button

    private lateinit var auth: FirebaseAuth
    private val staffRef = FirebaseDatabase.getInstance().getReference("Staff")
    private lateinit var dbHelper: StaffDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_staff)

        dbHelper    = StaffDbHelper(this)
        auth        = FirebaseAuth.getInstance()
        usernameEt  = findViewById(R.id.Username)
        emailEt     = findViewById(R.id.Email)
        passwordEt  = findViewById(R.id.Password)
        phoneEt     = findViewById(R.id.PhoneNumber)
        registerBtn = findViewById(R.id.Register)

        registerBtn.setOnClickListener { saveStaffData() }
        findViewById<Button>(R.id.Login).setOnClickListener {
            startActivity(Intent(this, StaffLoginActivity::class.java))
        }
    }

    private fun saveStaffData() {
        val uname = usernameEt.text.toString().trim()
        val mail  = emailEt.text.toString().trim()
        val pass  = passwordEt.text.toString().trim()
        val phone = phoneEt.text.toString().trim()

        if (uname.isEmpty() || mail.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Build local Staff with a random local ID
        val localId = UUID.randomUUID().toString()
        val staff = Staff(
            id       = localId,
            username = uname,
            email    = mail,
            password = pass,
            phone    = phone,
            name     = "",
            dp       = "",
            isSynced = 0
        )

        // 2) Save locally
        dbHelper.insertOrUpdate(staff)

        // 3) If offline, bail out
        if (!isConnected()) {
            Toast.makeText(
                this,
                "Staff registered offline. It will sync when online.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // 4) Otherwise register online
        auth.createUserWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Auth failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnCompleteListener
                }
                val uid = auth.currentUser!!.uid
                val remoteStaff = staff.copy(id = uid, isSynced = 1)

                // 5) Save to realtime DB
                staffRef.child(uid)
                    .setValue(remoteStaff)
                    .addOnSuccessListener {
                        dbHelper.markSynced(localId)
                        Toast.makeText(
                            this,
                            "Staff Registered Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, StaffEditProfileActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Profile save failed. Will sync later.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
            }
    }

    private fun isConnected(): Boolean {
        val cm  = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
