package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageStaffActivity : AppCompatActivity() {
    private val staff = mutableListOf<String>()
    private lateinit var adapter: StaffAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_staff)

        // find views
        val tvTotalStaff  = findViewById<TextView>(R.id.tvTotalStaff)
        val rvStaff       = findViewById<RecyclerView>(R.id.rvStaff)
        val btnAddStaff   = findViewById<Button>(R.id.btnAddStaff)

        // setup RecyclerView + adapter
        adapter = StaffAdapter(staff) { pos ->
            staff.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            tvTotalStaff.text = "Total: ${staff.size}"
        }
        rvStaff.layoutManager = LinearLayoutManager(this)
        rvStaff.adapter       = adapter

        // preload dummy data
        listOf("John","Mary","Steve").forEach {
            staff.add(it)
        }
        adapter.notifyDataSetChanged()
        tvTotalStaff.text = "Total: ${staff.size}"

        // button handlers
        btnAddStaff.setOnClickListener {
            Toast.makeText(this, "Add Staff clicked", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, RegisterStaffActivity::class.java)
            )
        }
    }
}
