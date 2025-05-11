// ManageStaffActivity.kt
package com.arshman.mahad.rehan

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arshman.mahad.rehan.ApiService
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ManageStaffActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService
    private lateinit var adapter: StaffAdapter
    private val staffList = mutableListOf<Staff>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_staff)

        // Firebase
        database = FirebaseDatabase.getInstance().getReference("Staff")

        // Retrofit
        val baseUrl = getString(R.string.base_url)
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // RecyclerView
        val tvTotalStaff = findViewById<TextView>(R.id.tvTotalStaff)
        val rvStaff      = findViewById<RecyclerView>(R.id.rvStaff)
        adapter = StaffAdapter(staffList) { pos ->
            deleteStaffFromDatabase(staffList[pos].id, pos, tvTotalStaff)
        }
        rvStaff.layoutManager = LinearLayoutManager(this)
        rvStaff.adapter = adapter

        // Load staff
        loadStaff(tvTotalStaff)

        // Add staff button
        findViewById<Button>(R.id.btnAddStaff).setOnClickListener {
            Toast.makeText(this, "Add Staff clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStaff(tvTotalStaff: TextView) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffList.clear()
                for (child in snapshot.children) {
                    child.getValue(Staff::class.java)?.let { staff ->
                        staffList.add(staff)
                        // fetch image URL and update dp
                        lifecycleScope.launch {
                            try {
                                val resp = api.getProfile(staff.id)
                                if (resp.isSuccessful && resp.body()?.status == "success") {
                                    resp.body()?.image_url?.let { url ->
                                        staff.dp = url
                                        val idx = staffList.indexOf(staff)
                                        if (idx >= 0) adapter.notifyItemChanged(idx)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("ManageStaff", "Failed to fetch image for ${staff.id}", e)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                tvTotalStaff.text = "Total: ${staffList.size}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ManageStaffActivity,
                    "Failed to load staff: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteStaffFromDatabase(staffId: String, position: Int, tvTotalStaff: TextView) {
        database.child(staffId).removeValue()
            .addOnSuccessListener {
                staffList.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, staffList.size)
                tvTotalStaff.text = "Total: ${staffList.size}"
                Toast.makeText(this, "Staff removed successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to remove staff: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
