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

data class Member(var id: String = "", var name: String = "", var dp: String = "")

class MemberAdapter(
    private val items: MutableList<Member>,
    private val onRemove: (position: Int) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView              = view.findViewById(R.id.tvMemberName)
        val imgProfile: CircleImageView   = view.findViewById(R.id.imgMemberProfile)
        val btnRemove: ImageButton        = view.findViewById(R.id.btnRemoveMember)

        init {
            btnRemove.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onRemove(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = items[position]
        holder.tvName.text = member.name

        // New logic: if dp is a URL, load via Glide, else Base64 decode
        val dp = member.dp
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
}
