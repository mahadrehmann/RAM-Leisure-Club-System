package com.arshman.mahad.rehan

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MemberHomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var roleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_home_page)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")

        // Get current user ID
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val profile = findViewById<LinearLayout>(R.id.llProfile)
        profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val book = findViewById<LinearLayout>(R.id.cardBook)
        book.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        val bills = findViewById<LinearLayout>(R.id.cardBills)
        bills.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        val cardMembership = findViewById<LinearLayout>(R.id.cardMembership)
        cardMembership.setOnClickListener {
            val intent = Intent(this, MembershipActivity::class.java)
            startActivity(intent)
        }

        val profileSettings = findViewById<LinearLayout>(R.id.cardProfileSettings)
        profileSettings.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize views
        profileImageView = findViewById(R.id.imgProfile)
        nameTextView = findViewById(R.id.tvName)
        roleTextView = findViewById(R.id.tvRole)

        // Load user data
        loadUserData(userId)
    }

    private fun loadUserData(userId: String) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        nameTextView.text = it.name
                        roleTextView.text = "Club Member" // Static role for now

                        // Decode and set profile picture
                        if (it.dp.isNotEmpty()) {
                            val decodedImage = Base64.decode(it.dp, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                            profileImageView.setImageBitmap(bitmap)
                        }
                    }
                } else {
                    Toast.makeText(this@MemberHomePage, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MemberHomePage, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}