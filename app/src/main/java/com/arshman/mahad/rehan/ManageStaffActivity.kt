package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageStaffActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var adapter: StaffAdapter
    private val staffList = mutableListOf<Staff>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_staff)

        val tvTotalStaff = findViewById<TextView>(R.id.tvTotalStaff)
        val rvStaff = findViewById<RecyclerView>(R.id.rvStaff)
        val btnAddStaff = findViewById<Button>(R.id.btnAddStaff)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Staff")

        // Set up RecyclerView
        adapter = StaffAdapter(staffList) { position ->
            val staff = staffList[position]
            deleteStaffFromDatabase(staff.id, position, tvTotalStaff)
        }
        rvStaff.layoutManager = LinearLayoutManager(this)
        rvStaff.adapter = adapter

        // Load staff from the database
        loadStaff(tvTotalStaff)

        btnAddStaff.setOnClickListener {
            Toast.makeText(this, "Add Staff clicked", Toast.LENGTH_SHORT).show()
            // Navigate to staff registration activity
        }
    }

    private fun loadStaff(tvTotalStaff: TextView) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffList.clear()
                for (child in snapshot.children) {
                    val staff = child.getValue(Staff::class.java)
                    staff?.let { staffList.add(it) }
                }
                adapter.notifyDataSetChanged()
                tvTotalStaff.text = "Total: ${staffList.size}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageStaffActivity, "Failed to load staff: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteStaffFromDatabase(staffId: String, position: Int, tvTotalStaff: TextView) {
        database.child(staffId).removeValue()
            .addOnSuccessListener {
                if (position >= 0 && position < staffList.size) {
                    staffList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    tvTotalStaff.text = "Total: ${staffList.size}"
                    Toast.makeText(this, "Staff removed successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid position", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove staff: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}