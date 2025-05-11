// StaffAdapter.kt
package com.arshman.mahad.rehan

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class StaffAdapter(
    private val items: MutableList<Staff>,
    private val onRemove: (position: Int) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    inner class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView            = view.findViewById(R.id.tvStaffName)
        val imgProfile: CircleImageView = view.findViewById(R.id.imgMemberProfile)
        val btnRemove: ImageButton      = view.findViewById(R.id.btnRemoveStaff)

        init {
            btnRemove.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onRemove(pos)
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

        // Load dp: URL via Glide or Base64 fallback
        val dp = staff.dp
        if (dp.startsWith("http")) {
            Glide.with(holder.imgProfile.context)
                .load(dp)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(holder.imgProfile)
        } else if (dp.isNotEmpty()) {
            try {
                val decoded = Base64.decode(dp, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                holder.imgProfile.setImageBitmap(bmp)
            } catch (e: Exception) {
                holder.imgProfile.setImageResource(R.drawable.ic_profile)
            }
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_profile)
        }
    }

    override fun getItemCount(): Int = items.size
}
