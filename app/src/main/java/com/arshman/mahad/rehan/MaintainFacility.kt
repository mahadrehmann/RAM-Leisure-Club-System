package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MaintainFacility : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: MaintainFacilityAdapter
    private val maintenanceList = mutableListOf<FacilityMaintenance>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintain_facility)

        val recyclerView = findViewById<RecyclerView>(R.id.rvFacilities)
        database = FirebaseDatabase.getInstance().getReference("FacilityMaintenance")

        val schmaintenance = findViewById<Button>(R.id.btnSchedule)
        schmaintenance.setOnClickListener {
            // Navigate to ScheduleMaintenance activity
            startActivity(Intent(this, ScheduleMaintenance::class.java))
        }

        // Set up RecyclerView
        adapter = MaintainFacilityAdapter(maintenanceList) { maintenance ->
            removeMaintenance(maintenance)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load maintenance records from Firebase
        loadMaintenanceRecords()
    }

    private fun loadMaintenanceRecords() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                maintenanceList.clear()
                for (child in snapshot.children) {
                    val maintenance = child.getValue(FacilityMaintenance::class.java)
                    maintenance?.let { maintenanceList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MaintainFacility, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeMaintenance(maintenance: FacilityMaintenance) {
        database.child(maintenance.id).removeValue()
            .addOnSuccessListener {
                maintenanceList.remove(maintenance)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Maintenance done for ${maintenance.facility}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove item: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}