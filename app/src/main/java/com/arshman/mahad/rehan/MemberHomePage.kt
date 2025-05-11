package com.arshman.mahad.rehan

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MemberHomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var roleTextView: TextView

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_home_page)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val baseUrl = getString(R.string.base_url)
        Log.d("BASE_URL_CHECK", "Base URL is: $baseUrl")

        // Retrofit for image API
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        // Navigation
        findViewById<LinearLayout>(R.id.llProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardBook).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardBills).setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardMembership).setOnClickListener {
            startActivity(Intent(this, MembershipActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardProfileSettings).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Views
        profileImageView = findViewById(R.id.imgProfile)
        nameTextView     = findViewById(R.id.tvName)
        roleTextView     = findViewById(R.id.tvRole)

        // Load data
        loadUserData()
        loadProfileImage()
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(User::class.java)?.let { user ->
                        nameTextView.text = user.name
                        roleTextView.text = "Club Member"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MemberHomePage,
                        "Failed to load data: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status == "success") {
                    resp.body()?.image_url?.let { url ->
                        Glide.with(this@MemberHomePage)
                            .load(url)
                            .placeholder(R.drawable.plus_sign)
                            .circleCrop()
                            .into(profileImageView)
                    }
                } else {
                    // Fallback to Firebase-stored Base64 dp
                    loadFirebaseDp()
                }
            } catch (e: Exception) {
                // Network error or parsing error; fallback
                loadFirebaseDp()
            }
        }
    }

    private fun loadFirebaseDp() {
        database.child(userId).child("dp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dp = snapshot.getValue(String::class.java) ?: return
                    if (dp.startsWith("http")) {
                        Glide.with(this@MemberHomePage)
                            .load(dp)
                            .placeholder(R.drawable.plus_sign)
                            .circleCrop()
                            .into(profileImageView)
                    } else if (dp.isNotEmpty()) {
                        val decoded = android.util.Base64.decode(dp, android.util.Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                        profileImageView.setImageBitmap(bmp)
                    }
                }
                override fun onCancelled(error: DatabaseError) { /* no-op */ }
            })
    }
}
