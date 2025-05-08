// RegisterActivity.kt
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var registerBtn: Button

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
        .getReference("Members")
    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper     = UserDbHelper(this)
        auth         = FirebaseAuth.getInstance()
        usernameEt   = findViewById(R.id.Username)
        emailEt      = findViewById(R.id.Email)
        passwordEt   = findViewById(R.id.Password)
        phoneEt      = findViewById(R.id.PhoneNumber)
        registerBtn  = findViewById(R.id.Register)

        registerBtn.setOnClickListener { saveUserData() }

        findViewById<Button>(R.id.Login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun saveUserData() {
        val uname = usernameEt.text.toString().trim()
        val mail  = emailEt.text.toString().trim()
        val pass  = passwordEt.text.toString().trim()
        val phone = phoneEt.text.toString().trim()

        if (uname.isEmpty() || mail.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Build local User object
        val localId = UUID.randomUUID().toString()
        val user = User(
            id         = localId,
            username   = uname,
            email      = mail,
            password   = pass,
            phone      = phone,
            name       = "",
            dp         = "",
            membership = "regular",
            isSynced   = 0
        )

        // 2) Save offline
        dbHelper.insertOrUpdate(user)

        // 3) If offline, finish immediately
        if (!isConnected()) {
            Toast.makeText(
                this,
                "Registered offline. It will sync when you’re back online.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // 4) Otherwise perform online signup
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
                // 5) On success, push profile to Realtime DB
                val uid = auth.currentUser!!.uid
                val remoteUser = user.copy(id = uid, isSynced = 1)
                database.child(uid)
                    .setValue(remoteUser)
                    .addOnSuccessListener {
                        // 6) Mark local record synced
                        dbHelper.markSynced(localId)
                        Toast.makeText(
                            this,
                            "User Registered Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, EditProfileActivity::class.java))
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
