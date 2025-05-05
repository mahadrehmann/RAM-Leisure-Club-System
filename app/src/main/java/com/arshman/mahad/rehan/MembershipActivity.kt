package com.arshman.mahad.rehan

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MembershipActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userId: String // Assume this is retrieved from the logged-in user session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        database = FirebaseDatabase.getInstance().reference
        userId = "USER_ID" // Replace with actual user ID from authentication/session

        val radioGroupPlans = findViewById<RadioGroup>(R.id.radioGroupPlans)
        val btnUpdateMembership = findViewById<Button>(R.id.btnUpdateMembership)

        btnUpdateMembership.setOnClickListener {
            val selectedPlanId = radioGroupPlans.checkedRadioButtonId

            if (selectedPlanId != -1) {
                val selectedPlan = when (selectedPlanId) {
                    R.id.radioPlanBasic -> "Basic"
                    R.id.radioPlanPro -> "Pro"
                    R.id.radioPlanPremium -> "Premium"
                    else -> null
                }

                selectedPlan?.let {
                    updateMembership(it)
                }
            } else {
                Toast.makeText(this, "Please select a membership plan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMembership(plan: String) {
        // Update membership in the User table
        database.child("User").child(userId).child("membership").setValue(plan)
            .addOnSuccessListener {
                addPayment(plan)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update membership: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addPayment(plan: String) {
        // Generate a unique ID for the payment
        val paymentId = database.child("Payment").push().key ?: return

        // Define payment amount based on the plan
        val amount = when (plan) {
            "Basic" -> "10"
            "Pro" -> "20"
            "Premium" -> "30"
            else -> "0"
        }

        // Create a Payment object
        val payment = Payment(
            id = paymentId,
            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            type = plan,
            amount = amount
        )

        // Save payment to the Payment table
        database.child("Payment").child(paymentId).setValue(payment)
            .addOnSuccessListener {
                Toast.makeText(this, "Membership updated and payment added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add payment: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}