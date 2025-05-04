package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MembershipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        val radioGroupPlans = findViewById<RadioGroup>(R.id.radioGroupPlans)
        val btnUpdateMembership = findViewById<Button>(R.id.btnUpdateMembership)

        btnUpdateMembership.setOnClickListener {
            val selectedPlanId = radioGroupPlans.checkedRadioButtonId

            if (selectedPlanId != -1) {
                // Navigate to Payment Activity
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a membership plan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}