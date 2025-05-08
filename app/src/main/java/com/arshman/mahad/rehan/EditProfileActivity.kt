package com.arshman.mahad.rehan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
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
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var nameEt: EditText
    private lateinit var usernameEt: EditText
    private lateinit var contactEt: EditText
    private lateinit var updateButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var api: ApiService
    private var userId: String = ""

    private var selectedImageUri: Uri? = null

    private val pickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                val bmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                profileImage.setImageBitmap(bmp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Firebase refs
        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        userId   = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Retrofit init
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.94.111/RAMsolutions/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        // UI
        profileImage = findViewById(R.id.ProfilePicture)
        nameEt        = findViewById(R.id.nameEditText)
        usernameEt    = findViewById(R.id.usernameEditText)
        contactEt     = findViewById(R.id.contactEditText)
        updateButton  = findViewById(R.id.myBtn)

        // Load text fields from Firebase + image from server
        loadUserData()
        loadProfileImage()

        profileImage.setOnClickListener { openImagePicker() }
        updateButton.setOnClickListener { updateUserData() }
    }

    private fun loadUserData() {
        database.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    snap.getValue(User::class.java)?.let { u ->
                        nameEt.setText(u.name)
                        usernameEt.setText(u.username)
                        contactEt.setText(u.phone)
                    }
                }
                override fun onCancelled(e: DatabaseError) {
                    Toast.makeText(this@EditProfileActivity, "Load failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val resp = api.getProfile(userId)
                if (resp.isSuccessful && resp.body()?.status == "success") {
                    resp.body()?.image_url?.let { url ->
                        Glide.with(this@EditProfileActivity)
                            .load(url)
                            .placeholder(R.drawable.plus_sign)
                            .into(profileImage)
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "GET image failed", e)
            }
        }
    }

    private fun openImagePicker() {
        pickerLauncher.launch(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )
    }

    private fun updateUserData() {
        val updatedName     = nameEt.text.toString().trim()
        val updatedUsername = usernameEt.text.toString().trim()
        val updatedContact  = contactEt.text.toString().trim()

        // 1) Update text fields in Firebase
        database.child(userId).updateChildren(mapOf(
            "name"     to updatedName,
            "username" to updatedUsername,
            "phone"    to updatedContact
        ))

        // 2) If an image was picked, upload it
        selectedImageUri?.let { uri ->
            lifecycleScope.launch {
                try {
                    // Convert URI → temp File
                    val tempFile = File(cacheDir, "upload_${UUID.randomUUID()}.jpg")
                    withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part    = MultipartBody.Part.createFormData(
                        "image", tempFile.name, reqFile
                    )
                    val userBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                    val resp = api.uploadProfileImage(userBody, part)
                    if (resp.isSuccessful && resp.body()?.status == "success") {
                        resp.body()?.image_url?.let { url ->
                            // 3) Save returned URL into Firebase dp field
                            database.child(userId).child("dp").setValue(url)
                        }
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditProfile", "POST image failed", e)
                    Toast.makeText(this@EditProfileActivity, " ${e.message} Image upload error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
        finish()
    }
}
