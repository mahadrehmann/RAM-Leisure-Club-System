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

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService

    private lateinit var profileImageView: CircleImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Retrofit
        val baseUrl = getString(R.string.base_url)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        // Views
        profileImageView   = findViewById(R.id.ProfilePicture)
        nameTextView       = findViewById(R.id.tvUserName)
        emailTextView      = findViewById(R.id.tvEmail)
        phoneTextView      = findViewById(R.id.tvPhone)
        logoutButton       = findViewById(R.id.btnLogout)
        editProfileButton  = findViewById(R.id.btnEditProfile)

        // Load data
        loadUserData()
        loadProfileImage()

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        nameTextView.text  = user.name
                        emailTextView.text = user.email
                        phoneTextView.text = user.phone
                    } else {
                        Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status == "success") {
                    resp.body()?.image_url?.let { url ->
                        Glide.with(this@ProfileActivity)
                            .load(url)
                            .placeholder(R.drawable.plus_sign)
                            .circleCrop()
                            .into(profileImageView)
                        return@launch
                    }
                }
            } catch (_: Exception) { /* ignore */ }

            // Fallback to Firebase Base64 dp
            database.child(userId).child("dp")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {
                        val dp = snap.getValue(String::class.java) ?: return
                        if (dp.startsWith("http")) {
                            Glide.with(this@ProfileActivity)
                                .load(dp)
                                .placeholder(R.drawable.plus_sign)
                                .circleCrop()
                                .into(profileImageView)
                        } else if (dp.isNotEmpty()) {
                            val decoded = Base64.decode(dp, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            profileImageView.setImageBitmap(bmp)
                        }
                    }
                    override fun onCancelled(e: DatabaseError) { /* no-op */ }
                })
        }
    }
}
