package com.example.talk_in

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var nameOfUser: TextView
    private lateinit var sendButton: ImageView
    private lateinit var backbtnImage: ImageView
    private lateinit var userprofileImage: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var receiverUid: String
    private lateinit var senderName: String
    private var senderUid: String? = null
    private var isReplyingFromNotification: Boolean = false
    private lateinit var storageReference: StorageReference

    companion object {
        var currentChatUser: String? = null
        var isActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        Log.d("ChatActivity", "onCreate() method called")

        val name = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid") ?: ""
        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = "$receiverUid$senderUid"
        receiverRoom = "$senderUid$receiverUid"
        supportActionBar?.hide()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        nameOfUser = findViewById(R.id.nameOfUser)
        backbtnImage = findViewById(R.id.backbtnImage)
        userprofileImage = findViewById(R.id.userprofileImage)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        nameOfUser.text = name
        setProfileImage(receiverUid)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Fetch sender's name
        senderUid?.let {
            mDbRef.child("user").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    senderName = dataSnapshot.child("name").value.toString()
                    loadChatMessages()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        val replyMessage = intent.getStringExtra("replyMessage")
        if (!replyMessage.isNullOrEmpty() && intent.getBooleanExtra("fromNotification", false)) {
            // If a reply message is received from notification, send it to the receiver
            isReplyingFromNotification = true
            val messageObject = Message(AESUtils.encrypt(replyMessage), senderUid, System.currentTimeMillis())
            sendMessage(messageObject)
        }

        messageBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (messageBox.text.toString().isNotEmpty()){
                    sendButton.setImageResource(R.drawable.send_icon_dark)
                }
                else{
                    sendButton.setImageResource(R.drawable.send_icon_dull)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(editable: Editable?) {}
        })

        // Adding message to database
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(AESUtils.encrypt(messageText), senderUid, System.currentTimeMillis())
                sendMessage(messageObject)
                messageBox.setText("")
            }
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
                    val intent = Intent(this@ChatActivity, UserProfileScreen::class.java)
                    intent.putExtra("MODE", "RECEIVER_USER")
                    intent.putExtra("RECEIVER_UID", receiverUid)
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
        Log.d("ChatActivity", "loadChatMessages() method called")
        mDbRef.child("chats").child(senderRoom!!)
            .child("messages")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    var currentDate: String? = null
                    for (postSnapshot in snapshot.children) {
                        var message = postSnapshot.getValue(Message::class.java)
                        message?.let {
                            // Get the date from the timestamp
                            val date = getDateFromTimestamp(it.timestamp ?: 0L)

                            // If the date changes, add a new message with the date as a separator
                            if (date != currentDate) {
                                currentDate = date
                                // Add separator message for the date section
                                messageList.add(Message(date, "", null, true))
                            }
                            messageList.add(it)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatActivity", "loadChatMessages() onCancelled: ${error.message}")
                }
            })
    }

    private fun getDateFromTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun sendMessage(messageObject: Message) {
        mDbRef.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
                    .addOnSuccessListener {
                        sendNotificationToReceiver(receiverUid, messageObject.message!!)
                    }
                    .addOnFailureListener {
                        Log.e("ChatActivity", "Failed to send message to receiver: ${it.message}")
                        Toast.makeText(this, "Failed to send message to receiver.", Toast.LENGTH_SHORT).show()
                    }

                if (isReplyingFromNotification) {
                    isReplyingFromNotification = false
                }
            }
            .addOnFailureListener {
                Log.e("ChatActivity", "Failed to send message: ${it.message}")
                Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotificationToReceiver(receiverUid: String, message: String) {
        // Fetch receiver's device token
        mDbRef.child("users-device-tokens").child(receiverUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val receiverDeviceToken = dataSnapshot.child("deviceToken").value.toString()

                // Decrypt the message if it's in valid hexadecimal format
                val decryptedMessage = try {
                    if (isHex(message)) {
                        AESUtils.decrypt(message)
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

                val notificationSender = FcmNotificationsSender(
                    receiverDeviceToken,
                    "New Message from $senderName",
                    decryptedMessage,
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    senderName,
                    applicationContext,
                    this@ChatActivity
                )
                notificationSender.sendNotifications()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChatActivity", "sendNotificationToReceiver() onCancelled: ${databaseError.message}")
            }
        })
    }

    // Helper function to check if a string is in valid hexadecimal format
    private fun isHex(str: String): Boolean {
        return str.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }


    private fun setProfileImage(currentUserUid: String) {
        storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images")
            .child("$currentUserUid.jpg")
        try {
            val localFile = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(resources, bitmap)
                    circularBitmapDrawable.isCircular = true
                    userprofileImage.setImageDrawable(circularBitmapDrawable)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    public fun showContextMenu(anchorView: View, message: Message) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.custom_context_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupView.findViewById<TextView>(R.id.action_delete_for_you).setOnClickListener {
            deleteMessageForYou(message)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.action_reply).setOnClickListener {
            replyToMessage(message)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.action_copy).setOnClickListener {
            copyMessageToClipboard(message)
            popupWindow.dismiss()
        }

        // Check if the current user is the sender and if the message is not deleted
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (message.senderId == currentUserUid && !message.isDeleted) {
            popupView.findViewById<TextView>(R.id.action_deleted_for_everyone).setOnClickListener {
                deleteMessageForEveryone(message)
                popupWindow.dismiss()
            }
        } else {
            popupView.findViewById<TextView>(R.id.action_deleted_for_everyone).visibility = View.GONE
        }

        popupWindow.showAsDropDown(anchorView)
    }

    private fun deleteMessageForEveryone(message: Message) {
        if (senderRoom == null || receiverRoom == null) {
            Toast.makeText(this, "Error: Cannot delete message. Room not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val senderMessagesRef = mDbRef.child("chats").child(senderRoom!!).child("messages")
        val receiverMessagesRef = mDbRef.child("chats").child(receiverRoom!!).child("messages")


        val senderQuery = senderMessagesRef.orderByChild("timestamp").equalTo(message.timestamp?.toDouble() ?: 0.0)

        // Listener for sender's chat
        senderQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(senderSnapshot: DataSnapshot) {
                if (senderSnapshot.exists()) {
                    for (senderChildSnapshot in senderSnapshot.children) {
                        val senderMsg = senderChildSnapshot.getValue(Message::class.java)
                        if (senderMsg != null && senderMsg.senderId == message.senderId && senderMsg.timestamp == message.timestamp) {
                            val senderPlaceholder = Message(
                                senderId = message.senderId,
                                timestamp = message.timestamp,
                                message = AESUtils.encrypt("You deleted this message"), // Encrypt message
                                isDeleted = true,
                                messageType = "deleted_sender"
                            )

                            senderChildSnapshot.ref.removeValue().addOnSuccessListener {
                                // Update local list and UI for sender
                                messageList.find { it.timestamp == message.timestamp && it.senderId == message.senderId }?.let { originalMessage ->
                                    val index = messageList.indexOf(originalMessage)
                                    messageList[index] = senderPlaceholder
                                    messageAdapter.notifyDataSetChanged()
                                }

                                // Query to find the message to delete in receiver's chat
                                val receiverQuery = receiverMessagesRef.orderByChild("timestamp").equalTo(message.timestamp?.toDouble() ?: 0.0)

                                // Listener for receiver's chat
                                receiverQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(receiverSnapshot: DataSnapshot) {
                                        if (receiverSnapshot.exists()) {
                                            for (receiverChildSnapshot in receiverSnapshot.children) {
                                                val receiverMsg = receiverChildSnapshot.getValue(Message::class.java)
                                                if (receiverMsg != null && receiverMsg.senderId == message.senderId && receiverMsg.timestamp == message.timestamp) {
                                                    // Encrypt placeholder message for receiver
                                                    val receiverPlaceholder = Message(
                                                        senderId = message.senderId,
                                                        timestamp = message.timestamp,
                                                        message = AESUtils.encrypt("This message was deleted"), // Encrypt message
                                                        isDeleted = true,
                                                        messageType = "deleted_receiver"
                                                    )

                                                    // Remove the message from receiver's chat
                                                    receiverChildSnapshot.ref.removeValue().addOnSuccessListener {
                                                        Toast.makeText(this@ChatActivity, "Message deleted", Toast.LENGTH_SHORT).show()

                                                        // Update local list and UI for receiver
                                                        messageList.find { it.timestamp == message.timestamp && it.senderId != message.senderId }?.let { originalMessage ->
                                                            val index = messageList.indexOf(originalMessage)
                                                            messageList[index] = receiverPlaceholder
                                                            messageAdapter.notifyDataSetChanged()
                                                        }
                                                    }.addOnFailureListener {
                                                        Toast.makeText(this@ChatActivity, "Error: Could not delete message for receiver.", Toast.LENGTH_SHORT).show()
                                                    }
                                                    break
                                                }
                                            }
                                        } else {
                                            Toast.makeText(this@ChatActivity, "Error: Message not found in receiver's chat.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(receiverDatabaseError: DatabaseError) {
                                        Toast.makeText(this@ChatActivity, "Error: ${receiverDatabaseError.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })

                            }.addOnFailureListener {
                                Toast.makeText(this@ChatActivity, "Error: Could not delete message from sender's chat.", Toast.LENGTH_SHORT).show()
                            }
                            break
                        }
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Error: Message not found in sender's chat.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(senderDatabaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Error: ${senderDatabaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }





    private fun replyToMessage(message: Message) {
        // Logic to reply to the message
        Toast.makeText(this, "Replying to message: ${message.message}", Toast.LENGTH_SHORT).show()
    }

    private fun copyMessageToClipboard(message: Message) {
        // Logic to copy the message to clipboard
        Toast.makeText(this, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMessageForYou(message: Message) {
        if (senderRoom == null || receiverRoom == null) {
            Toast.makeText(this, "Error: Cannot delete message. Room not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val userRoom = if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) senderRoom else receiverRoom
        val userMessagesRef = mDbRef.child("chats").child(userRoom!!).child("messages")

        // Query to find the message to delete for the current user
        val query = userMessagesRef.orderByChild("timestamp").equalTo(message.timestamp?.toDouble() ?: 0.0)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val msg = childSnapshot.getValue(Message::class.java)
                        if (msg != null) {
                            if (msg.senderId == message.senderId && msg.timestamp == message.timestamp) {
                                // Remove the message from the current user's view
                                childSnapshot.ref.removeValue().addOnSuccessListener {
                                    Toast.makeText(this@ChatActivity, "Message deleted for you", Toast.LENGTH_SHORT).show()

                                    // Remove the message from the local list and update the UI
                                    messageList.find { it.timestamp == message.timestamp && it.senderId == message.senderId }?.let { originalMessage ->
                                        val index = messageList.indexOf(originalMessage)
                                        messageList.removeAt(index)
                                        messageAdapter.notifyDataSetChanged()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(this@ChatActivity, "Error: Could not delete the message for you.", Toast.LENGTH_SHORT).show()
                                }
                                break
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Error: Message not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        currentChatUser = receiverUid
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        currentChatUser = null
        isActive = false
    }
}