// ScheduleMaintenance.kt
package com.arshman.mahad.rehan

import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ScheduleMaintenance : AppCompatActivity() {

    private lateinit var spinnerFacility: Spinner
    private lateinit var etDueDate: EditText
    private lateinit var btnSchedule: Button
    private lateinit var dbHelper: FacilityMaintenanceDbHelper
    private val firebaseRef = FirebaseDatabase.getInstance()
        .getReference("FacilityMaintenance")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maintenance)

        dbHelper = FacilityMaintenanceDbHelper(this)
        spinnerFacility = findViewById(R.id.spinnerFacility)
        etDueDate       = findViewById(R.id.etDueDate)
        btnSchedule     = findViewById(R.id.btnSchedule)

        setupFacilitySpinner()
        setupDatePicker()

        btnSchedule.setOnClickListener { submitMaintenance() }
    }

    private fun setupFacilitySpinner() {
        val facilities = listOf("Swimming", "Cricket", "Sauna", "Gym", "Tennis")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            facilities
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerFacility.adapter = adapter
    }

    private fun setupDatePicker() {
        etDueDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDueDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun submitMaintenance() {
        val facility = spinnerFacility.selectedItem as String
        val dueDate  = etDueDate.text.toString().trim()
        if (dueDate.isEmpty()) {
            etDueDate.error = "Please select a due date"
            return
        }

        val id = firebaseRef.push().key ?: UUID.randomUUID().toString()
        val m  = FacilityMaintenance(id, facility, dueDate, 0)

        // 1) Save locally
        dbHelper.insertOrUpdate(m)

        // 2) If offline, finish immediately
        if (!isConnected()) {
            Toast.makeText(
                this,
                "Maintenance scheduled offline. It will sync when online.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // 3) Upload to Firebase & mark synced
        firebaseRef.child(id)
            .setValue(m)
            .addOnSuccessListener {
                dbHelper.markSynced(id)
                Toast.makeText(this, "Scheduled $facility on $dueDate", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Saved offline. Will sync later.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
    }

    private fun isConnected(): Boolean {
        val cm  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetworkInfo
        return net != null && net.isConnected
    }
}
