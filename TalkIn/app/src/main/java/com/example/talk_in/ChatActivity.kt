package com.example.talk_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var nameOfUser: TextView
    private lateinit var sendButton: ImageView
    private lateinit var backbtnImage: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var receiveruid: String
    private lateinit var receiverUid: String
    private lateinit var senderName: String
    var receiverRoom: String?=null
    var senderRoom: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name= intent.getStringExtra("name")
        receiveruid= intent.getStringExtra("uid").toString()

        val senderUid= FirebaseAuth.getInstance().currentUser?.uid
        mDbRef= FirebaseDatabase.getInstance().getReference()
        senderRoom = "$receiverUid$senderUid"
        receiverRoom = "$senderUid$receiverUid"
        supportActionBar?.hide()

        chatRecyclerView=findViewById(R.id.chatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sendButton)
        nameOfUser=findViewById(R.id.nameOfUser)
        backbtnImage=findViewById(R.id.backbtnImage)
        messageList=ArrayList()
        messageAdapter=MessageAdapter(this,messageList)

        nameOfUser.setText(name)

        chatRecyclerView.layoutManager=LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter

        // Fetch sender's name
        senderUid?.let {
            mDbRef.child("user").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    senderName = dataSnapshot.child("name").value.toString()
                    // Load chat messages
                    loadChatMessages()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }

            })
        }

        // Adding message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                        .addOnSuccessListener {
                            sendNotificationToReceiver(receiverUid, message)
                        }
                }
            messageBox.setText("")
        }


        backbtnImage.setOnClickListener {
            val intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val popupMenuBtn: ImageView = findViewById(R.id.popupMenuBtn)
        popupMenuBtn.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.user_chat_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.viewProfile -> {
                    val intent = Intent(this, UserProfileScreen::class.java)
                    intent.putExtra("MODE", "RECEIVER_USER")
                    intent.putExtra("RECEIVER_UID", receiveruid)
                    startActivity(intent)
                    true
                }
                R.id.sharedMedia -> {
                    Toast.makeText(this, "Shared Media Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.locateNow -> {
                    Toast.makeText(this, "Locate Now Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun loadChatMessages() {
        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
                // Scroll RecyclerView to the bottom
                chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sendNotificationToReceiver(receiverUid: String, message: String) {
        // Fetch receiver's device token
        mDbRef.child("users-device-tokens").child(receiverUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val receiverDeviceToken = dataSnapshot.child("deviceToken").value.toString()

                val notificationSender = FcmNotificationsSender(
                    receiverDeviceToken,
                    "New Message from $senderName",
                    message,
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    senderName,
                    applicationContext,
                    this@ChatActivity
                )
                notificationSender.sendNotifications()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}