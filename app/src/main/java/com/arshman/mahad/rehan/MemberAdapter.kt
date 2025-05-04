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

data class Member(val id: String = "", val name: String = "", val dp: String = "")
class MemberAdapter(
    private val items: MutableList<Member>,
    private val onRemove: (position: Int) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMemberName)
        val imgProfile: CircleImageView = view.findViewById(R.id.imgMemberProfile)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveMember)

        init {
            btnRemove.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onRemove(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = items[position]
        holder.tvName.text = member.name

        // Load the profile picture (dp) if available
        if (member.dp.isNotEmpty()) {
            val decodedImage = Base64.decode(member.dp, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            holder.imgProfile.setImageBitmap(bitmap)
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_profile) // Default profile image
        }
    }

    override fun getItemCount(): Int = items.size
}