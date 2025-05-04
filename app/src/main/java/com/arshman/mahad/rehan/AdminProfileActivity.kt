package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var profileImageView: CircleImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Staff")

        // Get current user ID
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        profileImageView = findViewById(R.id.ProfilePicture)
        nameTextView = findViewById(R.id.tvUserName)
        emailTextView = findViewById(R.id.tvEmail)
        phoneTextView = findViewById(R.id.tvPhone)
        logoutButton = findViewById(R.id.btnLogout)
        editProfileButton = findViewById(R.id.btnEditProfile)


        // Load user data
        loadUserData(userId)

        // Handle edit profile button click
        editProfileButton.setOnClickListener {
            startActivity(Intent(this, AdminEditProfileActivity::class.java))
        }

        // Handle logout button click
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData(userId: String) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        nameTextView.text = it.name
                        emailTextView.text = it.email
                        phoneTextView.text = it.phone

                        // Decode and set profile picture
                        if (it.dp.isNotEmpty()) {
                            val decodedImage = Base64.decode(it.dp, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                            profileImageView.setImageBitmap(bitmap)
                        }
                    }
                } else {
                    Toast.makeText(this@AdminProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminProfileActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}