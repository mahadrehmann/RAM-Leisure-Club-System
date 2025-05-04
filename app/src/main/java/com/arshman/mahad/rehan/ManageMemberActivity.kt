package com.arshman.mahad.rehan
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageMemberActivity : AppCompatActivity() {
    private val members = mutableListOf<String>()  // will come from your DB
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_member)

        // find views
        val tvTotalMembers = findViewById<TextView>(R.id.tvTotalMembers)
        val rvMembers      = findViewById<RecyclerView>(R.id.rvMembers)
        val btnAddMember   = findViewById<Button>(R.id.btnAddMember)


        // setup RecyclerView + adapter
        adapter = MemberAdapter(members) { pos ->
            members.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            tvTotalMembers.text = "Total: ${members.size}"
        }
        rvMembers.layoutManager = LinearLayoutManager(this)
        rvMembers.adapter       = adapter

        // preload dummy data
        listOf("Alice","Bob","Charlie","Diana","Eve").forEach {
            members.add(it)
        }
        adapter.notifyDataSetChanged()
        tvTotalMembers.text = "Total: ${members.size}"

        // button handlers
        btnAddMember.setOnClickListener {
            Toast.makeText(this, "Add Member clicked", Toast.LENGTH_SHORT).show()
            // TODO: launch your AddMember flow
        }

    }
}
