package com.example.talk_in

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DisplayGroupAdapter(val context: Context, val groupList: ArrayList<Group>) :
    RecyclerView.Adapter<DisplayGroupAdapter.UserViewHolder>() {
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        val view: View =
            LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentGroup = groupList[position]

        var name = currentGroup.groupName!!.toString().trim()
        if (currentGroup.groupName?.length!! > 25)
            name = name.substring(0, minOf(name.length, 25)) + "..."

        val groupDescriptionObj = mDbRef.child("groups").child(currentGroup.groupId!!).child("groupDescription")
        groupDescriptionObj.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupDescriptionValue = snapshot.getValue(String::class.java)
                holder.groupDescription.text = groupDescriptionValue
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })


        holder.groupName.text = name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("NAME", currentGroup.groupName)
            intent.putExtra("GROUP_ID", currentGroup.groupId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.txt_name)
        val groupDescription: TextView = itemView.findViewById(R.id.txt_last_message)
    }
}