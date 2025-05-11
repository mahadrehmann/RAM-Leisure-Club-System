package com.arshman.mahad.rehan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arshman.mahad.rehan.ApiService
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ManageMemberActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var api: ApiService
    private lateinit var adapter: MemberAdapter
    private val members = mutableListOf<Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_member)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Members")

        // Initialize Retrofit with baseUrl from strings.xml
        val baseUrl = getString(R.string.base_url)
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Setup RecyclerView and adapter
        val rvMembers = findViewById<RecyclerView>(R.id.rvMembers)
        val tvTotal   = findViewById<TextView>(R.id.tvTotalMembers)
        adapter = MemberAdapter(members) { pos ->
            removeMember(members[pos].id, pos, tvTotal)
        }
        rvMembers.layoutManager = LinearLayoutManager(this)
        rvMembers.adapter = adapter

        // Load members
        loadMembers(tvTotal)

        // Add member button
        findViewById<Button>(R.id.btnAddMember).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loadMembers(tvTotal: TextView) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                members.clear()
                for (child in snapshot.children) {
                    child.getValue(Member::class.java)?.let { member ->
                        members.add(member)
                        // fetch server image_url and store into dp
                        lifecycleScope.launch {
                            try {
                                val resp = api.getProfile(member.id)
                                if (resp.isSuccessful && resp.body()?.status == "success") {
                                    resp.body()?.image_url?.let { url ->
                                        member.dp = url
                                        adapter.notifyItemChanged(members.indexOf(member))
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("ManageMember", "Failed to fetch image for ${member.id}", e)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                tvTotal.text = "Total: ${members.size}"
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ManageMemberActivity,
                    "Failed to load members: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun removeMember(memberId: String, position: Int, tvTotal: TextView) {
        database.child(memberId).removeValue()
            .addOnSuccessListener {
                members.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, members.size)
                tvTotal.text = "Total: ${members.size}"
                Toast.makeText(this, "Member removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to remove member: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
