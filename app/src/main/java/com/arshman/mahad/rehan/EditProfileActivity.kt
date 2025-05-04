package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        val name = findViewById<EditText>(R.id.nameEditText)
        val username= findViewById<EditText>(R.id.usernameEditText)
        val contact = findViewById<EditText>(R.id.contactEditText)

        val done = findViewById<Button>(R.id.myBtn)
        done.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))

        }

    }
}