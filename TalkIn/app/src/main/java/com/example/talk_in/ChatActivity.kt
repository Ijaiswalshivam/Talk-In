package com.example.talk_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    var receiverRoom: String?=null
    var senderRoom: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val name= intent.getStringExtra("name")
        val receiveruid= intent.getStringExtra("uid")
        val senderUid= FirebaseAuth.getInstance().currentUser?.uid
        mDbRef= FirebaseDatabase.getInstance().getReference()
        senderRoom= receiveruid+senderUid
        receiverRoom=senderUid+receiveruid
        supportActionBar?.title=name

        chatRecyclerView=findViewById(R.id.chatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sendButton)
        messageList=ArrayList()
        messageAdapter=MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager=LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter
        //logic to add data to recyclerview
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                   messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
       //adding message to data base
        sendButton.setOnClickListener{
         val message=messageBox.text.toString()
            val messageObject= Message(message,senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)

                }
            messageBox.setText("")
        }
    }
}