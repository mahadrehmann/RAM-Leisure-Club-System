package com.arshman.mahad.rehan

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arshman.mahad.rehan.ApiService
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService

    private lateinit var profileImageView: CircleImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_profile)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Admin")
        userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finish()
            return
        }

        // Retrofit
        val baseUrl = getString(R.string.base_url)
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Views
        profileImageView  = findViewById(R.id.ProfilePicture)
        nameTextView      = findViewById(R.id.tvUserName)
        emailTextView     = findViewById(R.id.tvEmail)
        phoneTextView     = findViewById(R.id.tvPhone)
        logoutButton      = findViewById(R.id.btnLogout)
        editProfileButton = findViewById(R.id.btnEditProfile)

        // Load
        loadUserData()
        loadProfileImage()

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, AdminEditProfileActivity::class.java))
        }
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    snap.getValue(User::class.java)?.let {
                        nameTextView.text  = it.name
                        emailTextView.text = it.email
                        phoneTextView.text = it.phone
                    }
                }
                override fun onCancelled(e: DatabaseError) {
                    Toast.makeText(this@AdminProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status == "success") {
                    resp.body()!!.image_url?.let { url ->
                        Glide.with(this@AdminProfileActivity)
                            .load(url)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImageView)
                        return@launch
                    }
                }
            } catch (_: Exception) { }
            // fallback Base64 dp
            database.child(userId).child("dp")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {
                        val dp = snap.getValue(String::class.java) ?: return
                        if (dp.startsWith("http")) {
                            Glide.with(this@AdminProfileActivity)
                                .load(dp)
                                .placeholder(R.drawable.ic_profile)
                                .circleCrop()
                                .into(profileImageView)
                        } else if (dp.isNotEmpty()) {
                            val decoded = Base64.decode(dp, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            profileImageView.setImageBitmap(bmp)
                        }
                    }
                    override fun onCancelled(e: DatabaseError) {}
                })
        }
    }
}
