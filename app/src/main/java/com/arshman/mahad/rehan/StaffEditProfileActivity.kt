package com.arshman.mahad.rehan

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arshman.mahad.rehan.ApiService
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.util.*

class StaffEditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var nameEt: EditText
    private lateinit var usernameEt: EditText
    private lateinit var contactEt: EditText
    private lateinit var updateButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService
    private lateinit var userId: String

    private var selectedUri: Uri? = null

    private val picker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            res.data?.data?.let {
                selectedUri = it
                val bmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
                profileImage.setImageBitmap(bmp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_edit_profile)

        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Staff")
        userId   = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        // Retrofit
        val baseUrl = getString(R.string.base_url)
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        profileImage = findViewById(R.id.ProfilePicture)
        nameEt        = findViewById(R.id.nameEditText)
        usernameEt    = findViewById(R.id.usernameEditText)
        contactEt     = findViewById(R.id.contactEditText)
        updateButton  = findViewById(R.id.myBtn)

        loadUserData()
        loadProfileImage()

        profileImage.setOnClickListener {
            picker.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
        updateButton.setOnClickListener { submit() }
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    snap.getValue(User::class.java)?.let {
                        nameEt.setText(it.name)
                        usernameEt.setText(it.username)
                        contactEt.setText(it.phone)
                    }
                }
                override fun onCancelled(e: DatabaseError) {}
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status=="success") {
                    resp.body()!!.image_url?.let { url ->
                        Glide.with(this@StaffEditProfileActivity)
                            .load(url)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage)
                        return@launch
                    }
                }
            } catch (_: Exception) {}
            // fallback
            database.child(userId).child("dp")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {
                        val dp = snap.getValue(String::class.java) ?: return
                        if (dp.startsWith("http")) {
                            Glide.with(this@StaffEditProfileActivity)
                                .load(dp)
                                .placeholder(R.drawable.ic_profile)
                                .circleCrop()
                                .into(profileImage)
                        } else if (dp.isNotEmpty()) {
                            val b = Base64.decode(dp, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
                            profileImage.setImageBitmap(bmp)
                        }
                    }
                    override fun onCancelled(e: DatabaseError) {}
                })
        }
    }

    private fun submit() {
        val nm = nameEt.text.toString().trim()
        val un = usernameEt.text.toString().trim()
        val ct = contactEt.text.toString().trim()

        database.child(userId)
            .updateChildren(mapOf("name" to nm, "username" to un, "phone" to ct))

        selectedUri?.let { uri ->
            lifecycleScope.launch {
                val tmp = File(cacheDir, "up_${UUID.randomUUID()}.jpg")
                withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { inp ->
                        FileOutputStream(tmp).use { out -> inp.copyTo(out) }
                    }
                }
                val part = MultipartBody.Part.createFormData(
                    "image",
                    tmp.name,
                    tmp.asRequestBody("image/*".toMediaTypeOrNull())
                )
                val uidRB = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                try {
                    val r = api.uploadProfileImage(uidRB, part)
                    if (r.isSuccessful && r.body()?.status=="success") {
                        r.body()!!.image_url?.let { url ->
                            database.child(userId).child("dp").setValue(url)
                        }
                    } else {
                        Toast.makeText(this@StaffEditProfileActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@StaffEditProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, StaffHomeActivity::class.java))
        finish()
    }
}
