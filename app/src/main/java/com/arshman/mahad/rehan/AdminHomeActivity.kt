package com.arshman.mahad.rehan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminHomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        val managemember = findViewById<LinearLayout>(R.id.cardManagemember)
        val managestaff = findViewById<LinearLayout>(R.id.cardManagestaff)
        val managefacility = findViewById<LinearLayout>(R.id.cardMaintainFacilities)
        val profileview = findViewById<LinearLayout>(R.id.llProfile)
         profileview.setOnClickListener {
             // Handle click for Profile View
             startActivity(Intent(this, AdminProfileActivity::class.java))
         }

        managemember.setOnClickListener {
            // Handle click for Manage Member
            startActivity(Intent(this, ManageMemberActivity::class.java))
        }

        managestaff.setOnClickListener {
            // Handle click for Manage Staff
            startActivity(Intent(this, ManageStaffActivity::class.java))
        }

        managefacility.setOnClickListener {
            // Handle click for Manage Facilities
            startActivity(Intent(this, MaintainFacility::class.java))

        }
    }
}