package com.arshman.mahad.rehan

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ScheduleMaintenance : AppCompatActivity() {

    private lateinit var spinnerFacility: Spinner
    private lateinit var etDueDate: EditText
    private lateinit var btnSchedule: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maintenance)

        spinnerFacility = findViewById(R.id.spinnerFacility)
        etDueDate = findViewById(R.id.etDueDate)
        btnSchedule = findViewById(R.id.btnSchedule)

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
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFacility.adapter = adapter
    }

    private fun setupDatePicker() {
        etDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etDueDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun submitMaintenance() {
        val facility = spinnerFacility.selectedItem as String
        val dueDate = etDueDate.text.toString().trim()

        if (dueDate.isEmpty()) {
            etDueDate.error = "Please select a due date"
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("FacilityMaintenance")
        val id = database.push().key ?: return

        val maintenance = FacilityMaintenance(id, facility, dueDate)

        database.child(id).setValue(maintenance)
            .addOnSuccessListener {
                Toast.makeText(this, "Scheduled $facility on $dueDate", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}