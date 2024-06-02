package com.example.talk_in

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val REPLY_ACTION = "com.example.talk_in.REPLY_ACTION"
        const val KEY_REPLY = "key_reply"
    }

    private lateinit var mDbRef: DatabaseReference

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == REPLY_ACTION) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                val messageText = remoteInput.getCharSequence(KEY_REPLY)?.toString()
                if (!messageText.isNullOrEmpty()) {
                    val receiverUid = intent.getStringExtra("uid")
                    val senderUid = FirebaseAuth.getInstance().currentUser?.uid


                    Log.d("NotificationReceiver", "Received reply: $messageText")

                    // Store the reply message in Firebase
                    mDbRef = FirebaseDatabase.getInstance().reference
                    val senderRoom = "$receiverUid$senderUid"
                    val receiverRoom = "$senderUid$receiverUid"

                    val messageObject = Message(messageText, senderUid, System.currentTimeMillis())

                    mDbRef.child("chats").child(senderRoom).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            mDbRef.child("chats").child(receiverRoom).child("messages").push()
                                .setValue(messageObject)
                                .addOnSuccessListener {
                                    // Send notification to receiver
                                    if (receiverUid != null && senderUid != null) {
                                        fetchSenderNameAndSendNotification(context, receiverUid, messageObject.message!!, senderUid)
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("NotificationReceiver", "Failed to send message to receiver: ${it.message}")
                                }
                        }
                        .addOnFailureListener {
                            Log.e("NotificationReceiver", "Failed to send message: ${it.message}")
                        }
                }
            }
        }
    }

    private fun fetchSenderNameAndSendNotification(context: Context, receiverUid: String, message: String, senderUid: String) {
        Log.d("NotificationReceiver", "fetchSenderNameAndSendNotification() method called")
        mDbRef.child("user").child(senderUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val senderName = dataSnapshot.child("name").value.toString()
                sendNotificationToReceiver(context, receiverUid, message, senderName)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationReceiver", "fetchSenderNameAndSendNotification() onCancelled: ${databaseError.message}")
            }
        })
    }

    private fun sendNotificationToReceiver(context: Context, receiverUid: String, message: String, senderName: String) {
        Log.d("NotificationReceiver", "sendNotificationToReceiver() method called")
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
                    context,
                    null
                )
                notificationSender.sendNotifications()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationReceiver", "sendNotificationToReceiver() onCancelled: ${databaseError.message}")
            }
        })
    }
}
