package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageMemberActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: MemberAdapter
    private val members = mutableListOf<Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_member)

        val tvTotalMembers = findViewById<TextView>(R.id.tvTotalMembers)
        val rvMembers = findViewById<RecyclerView>(R.id.rvMembers)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Members")

        // Set up RecyclerView
        adapter = MemberAdapter(members) { position ->
            val member = members[position]
            removeMemberFromDatabase(member.id, position, tvTotalMembers)
        }
        rvMembers.layoutManager = LinearLayoutManager(this)
        rvMembers.adapter = adapter

        // Load members from the database
        loadMembers(tvTotalMembers)

        val addmember = findViewById<Button>(R.id.btnAddMember)
        addmember.setOnClickListener {
            // Handle adding a new member
            // You can implement this part based on your requirements
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loadMembers(tvTotalMembers: TextView) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                members.clear()
                for (child in snapshot.children) {
                    val member = child.getValue(Member::class.java)
                    member?.let { members.add(it) }
                }
                adapter.notifyDataSetChanged()
                tvTotalMembers.text = "Total: ${members.size}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageMemberActivity, "Failed to load members: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeMemberFromDatabase(memberId: String, position: Int, tvTotalMembers: TextView) {
        if (position < 0 || position >= members.size) {
            Toast.makeText(this, "Invalid position", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(memberId).removeValue()
            .addOnSuccessListener {
                // Safely remove the member from the list
                if (position < members.size) {
                    members.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, members.size) // Update remaining items
                    tvTotalMembers.text = "Total: ${members.size}"
                    Toast.makeText(this, "Member removed successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove member: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}