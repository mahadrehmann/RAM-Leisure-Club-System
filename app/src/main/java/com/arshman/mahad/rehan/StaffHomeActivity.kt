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
import androidx.lifecycle.lifecycleScope
import com.arshman.mahad.rehan.ApiService
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StaffHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var roleTextView: TextView

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_home)

        // Firebase & Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Staff")
        userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, StaffLoginActivity::class.java))
            finish()
            return
        }

        // Retrofit init
        val baseUrl = getString(R.string.base_url)
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Navigation
        findViewById<LinearLayout>(R.id.llProfile).setOnClickListener {
            startActivity(Intent(this, StaffProfileActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardBook).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardProfileSettings).setOnClickListener {
            startActivity(Intent(this, StaffEditProfileActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardMaintainFacilities).setOnClickListener {
            startActivity(Intent(this, MaintainFacility::class.java))
        }

        // Views
        profileImageView = findViewById(R.id.imgProfile)
        nameTextView     = findViewById(R.id.tvName)
        roleTextView     = findViewById(R.id.tvRole)

        // Load
        loadUserData()
        loadProfileImage()
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    snap.getValue(User::class.java)?.let {
                        nameTextView.text = it.name
                        roleTextView.text = "Staff"
                    }
                }
                override fun onCancelled(e: DatabaseError) {
                    Toast.makeText(this@StaffHomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status=="success") {
                    resp.body()!!.image_url?.let { url ->
                        Glide.with(this@StaffHomeActivity)
                            .load(url)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImageView)
                        return@launch
                    }
                }
            } catch (_: Exception) {}
            // fallback Base64 dp
            database.child(userId).child("dp")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {
                        val dp = snap.getValue(String::class.java) ?: return
                        if (dp.startsWith("http")) {
                            Glide.with(this@StaffHomeActivity)
                                .load(dp)
                                .placeholder(R.drawable.ic_profile)
                                .circleCrop()
                                .into(profileImageView)
                        } else if (dp.isNotEmpty()) {
                            val b = Base64.decode(dp, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
                            profileImageView.setImageBitmap(bmp)
                        }
                    }
                    override fun onCancelled(e: DatabaseError) {}
                })
        }
    }
}
