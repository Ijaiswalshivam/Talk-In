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
import com.google.firebase.database.*
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
                        val decryptedMessage = try {
                            if (isHex(lastMessage?.message.toString())) {
                                AESUtils.decrypt(lastMessage?.message.toString())
                            } else {
                                "Invalid message format"
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.e("Decryption Error", "Failed to decrypt message: ${e.message}")
                            "Invalid message"
                        } catch (e: Exception) {
                            Log.e("Decryption Error", "An error occurred during decryption: ${e.message}")
                            "Error decrypting message"
                        }
                        holder.txt_last_message.text = decryptedMessage
                    } else {
                        var aboutUser = currentUser.aboutMe.toString().trim()
                        if (aboutUser.length > 30) {
                            aboutUser = aboutUser.substring(0, minOf(currentUser.aboutMe.toString().length, 30)).trim() + "..."
                        }
                        holder.txt_last_message.text = aboutUser
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
        val textName: TextView = itemView.findViewById(R.id.txt_name)
        val userprofileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        val txt_last_message: TextView = itemView.findViewById(R.id.txt_last_message)
    }

    private fun setProfileImage(uid: String, holder: UserViewHolder) {
        val storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images").child("$uid.jpg")

        try {
            val localFile = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.resources, bitmap)
                    circularBitmapDrawable.isCircular = true
                    holder.userprofileImage.setImageDrawable(circularBitmapDrawable)
                }.addOnFailureListener {
                    holder.userprofileImage.setImageResource(R.drawable.user_profile_icon)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isHex(str: String): Boolean {
        return str.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }
}
