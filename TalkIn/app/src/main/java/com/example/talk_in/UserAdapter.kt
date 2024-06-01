package com.example.talk_in

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var mDbRef: DatabaseReference
    private lateinit var senderUid: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderUid = mAuth.uid.toString()

        val view: View =
            LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        val chatRoom = senderUid + currentUser.uid

        holder.textName.text = currentUser.name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
        }

        currentUser.uid?.let { setProfileImage(it, holder) }

        mDbRef.child("chats").child(chatRoom)
            .child("messages")
            .orderByChild("timestamp")
            .limitToLast(1) // Limit to last message
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lastMessage = snapshot.children.first().getValue(Message::class.java)
                        holder.txt_last_message.setText(AESUtils.decrypt(lastMessage?.message.toString()))
                    }
                    else{
                        var aboutUser = currentUser.aboutMe.toString().trim()
                        if (aboutUser.length > 30){
                            aboutUser = aboutUser.substring(0, minOf(currentUser.aboutMe.toString().length, 30)).trim() + "..."
                        }
                        holder.txt_last_message.setText(aboutUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Last Message", "Failed to load messages")
                }
            })
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val userprofileImage = itemView.findViewById<ImageView>(R.id.user_profile_image)
        val txt_last_message = itemView.findViewById<TextView>(R.id.txt_last_message)
    }

    private fun setProfileImage(uid: String, holder: UserViewHolder) {
        storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images")
            .child("$uid.jpg")
        try {
            val localFile = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    holder.userprofileImage.setImageBitmap(bitmap)
                }.addOnFailureListener{
                    holder.userprofileImage.setImageResource(R.drawable.user_profile_icon)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}