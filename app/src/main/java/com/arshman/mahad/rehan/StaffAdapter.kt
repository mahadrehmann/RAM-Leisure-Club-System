package com.arshman.mahad.rehan

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class StaffAdapter(
    private val items: MutableList<Staff>,
    private val onRemove: (position: Int) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    inner class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStaffName)
        val imgProfile: CircleImageView = view.findViewById(R.id.imgMemberProfile)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveStaff)

        init {
            btnRemove.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onRemove(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = items[position]
        holder.tvName.text = staff.name

        // Load the profile picture (dp) if available
        if (staff.dp.isNotEmpty()) {
            val decodedImage = Base64.decode(staff.dp, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            holder.imgProfile.setImageBitmap(bitmap)
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_profile) // Default profile image
        }
    }

    override fun getItemCount(): Int = items.size
}