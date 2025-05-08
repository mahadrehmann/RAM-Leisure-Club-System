// MaintainFacility.kt
package com.arshman.mahad.rehan

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MaintainFacility : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var dbHelper: FacilityMaintenanceDbHelper
    private lateinit var adapter: MaintainFacilityAdapter
    private val maintenanceList = mutableListOf<FacilityMaintenance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintain_facility)

        dbHelper  = FacilityMaintenanceDbHelper(this)
        database  = FirebaseDatabase.getInstance().getReference("FacilityMaintenance")

        val recyclerView = findViewById<RecyclerView>(R.id.rvFacilities)
        adapter = MaintainFacilityAdapter(maintenanceList) { m -> removeMaintenance(m) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnSchedule).setOnClickListener {
            startActivity(Intent(this, ScheduleMaintenance::class.java))
        }

        if (isConnected()) {
            syncPendingMaintenance()
            loadFromFirebase()
        } else {
            loadFromCache()
        }
    }

    private fun syncPendingMaintenance() {
        val pending = dbHelper.getUnsynced()
        for (m in pending) {
            database.child(m.id)
                .setValue(m)
                .addOnSuccessListener { dbHelper.markSynced(m.id) }
        }
    }

    private fun loadFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                maintenanceList.clear()
                for (child in snapshot.children) {
                    val m = child.getValue(FacilityMaintenance::class.java) ?: continue
                    maintenanceList.add(m)
                    m.isSynced = 1
                    dbHelper.insertOrUpdate(m)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MaintainFacility,
                    "Failed to load data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadFromCache() {
        maintenanceList.clear()
        maintenanceList.addAll(dbHelper.getUnsynced())
        adapter.notifyDataSetChanged()
    }

    private fun removeMaintenance(m: FacilityMaintenance) {
        database.child(m.id).removeValue()
            .addOnSuccessListener {
                dbHelper.insertOrUpdate(m.copy(isSynced = 1))
                maintenanceList.remove(m)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Maintenance done for ${m.facility}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isConnected(): Boolean {
        val cm  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
