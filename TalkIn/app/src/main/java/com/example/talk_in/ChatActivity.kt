package com.example.talk_in

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var mapLoaction: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var senderlatitude:String
    private lateinit var senderlongitude:String

    private lateinit var receiverlatitude:String
    private lateinit var receiverlongitude:String

    var receiverRoom: String? = null
    var senderRoom: String? = null
    val receiveruid= intent.getStringExtra("uid")
    val senderUid= FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val name= intent.getStringExtra("name")

        mDbRef= FirebaseDatabase.getInstance().getReference()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        senderRoom= receiveruid+senderUid
        receiverRoom=senderUid+receiveruid
        supportActionBar?.title=name

        chatRecyclerView=findViewById(R.id.chatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sendButton)
        mapLoaction = findViewById(R.id.maplocation)
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

        mapLoaction.setOnClickListener {

            if (receiveruid.isNullOrEmpty()) {
                showNoLocationDialog()
            } else {
                getReceiverLocation(receiveruid)
            }
            getSenderLocation()

            directionBetweenTwoMap(senderlatitude, senderlongitude, receiverlatitude, receiverlongitude)


        }
    }

    private fun getSenderLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        senderlatitude = location.getLatitude().toString();
                        senderlongitude = location.getLongitude().toString();
                        Log.i(senderlatitude, senderlongitude)

                        updateSenderLocationInFirebase(senderUid, senderlatitude, senderlongitude)
                    }
                }
        }


    }

    private fun updateSenderLocationInFirebase(senderUid: String?, latitude: String, longitude: String) {
        val locationMap = mapOf("latitude" to latitude, "longitude" to longitude)
        mDbRef.child("location").child(senderUid!!).setValue(locationMap)
    }

    private fun getReceiverLocation(receiverUid: String) {
        mDbRef.child("location").child(receiverUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        receiverlatitude= snapshot.child("latitude").getValue(String::class.java).toString()
                        receiverlongitude= snapshot.child("longitude").getValue(String::class.java).toString()
                        if (!receiverlatitude.isNullOrEmpty() && !receiverlongitude.isNullOrEmpty()) {
                            return
                        } else {
                            showNoLocationDialog()
                        }
                    } else {
                        showNoLocationDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showNoLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("No location of receiver available")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun directionBetweenTwoMap(sourceLatitude: String, sourceLongitude: String, destinationLatitude: String, destinationLongitude: String) {

        val mapUri = Uri.parse("https://maps.google.com/maps?saddr=$sourceLatitude,$sourceLongitude&daddr=$destinationLatitude,$destinationLongitude")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val senderUid = FirebaseAuth.getInstance().currentUser?.uid
                getSenderLocation()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}

