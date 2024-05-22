package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileScreen : AppCompatActivity() {

    private lateinit var logoutBtn: Button
    private lateinit var nameOfUser: TextView
    private lateinit var aboutMeTextView: TextView
    private lateinit var editAboutIcon: ImageView
    private lateinit var editContactIcon: ImageView
    private lateinit var showLocationSection: LinearLayout
    private lateinit var showLocationToggleBtn: Switch
    private lateinit var emailid: TextView
    private lateinit var backBtn: ImageView
    private var USER_MODE: String? = null
    private var RECEIVER_UID: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var userObj: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_screen)

        logoutBtn = findViewById(R.id.logoutBtn)
        nameOfUser = findViewById(R.id.nameOfUser)
        aboutMeTextView = findViewById(R.id.aboutMeTextView)
        editAboutIcon = findViewById(R.id.editAboutIcon)
        editContactIcon = findViewById(R.id.editContactIcon)
        showLocationSection = findViewById(R.id.showLocationSection)
        showLocationToggleBtn = findViewById(R.id.showLocationToggleBtn)
        emailid = findViewById(R.id.emailid)
        backBtn = findViewById(R.id.backBtn)

        USER_MODE = intent.getStringExtra("MODE").toString()
//        Log.d("User Profile Info", USER_MODE!!)
        if(USER_MODE == "RECEIVER_USER"){
            RECEIVER_UID = intent.getStringExtra("RECEIVER_UID").toString()
//            Log.d("User Profile Info", RECEIVER_UID!!)
        }

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        var currentUserUid = mAuth.currentUser?.uid
        if (USER_MODE == "RECEIVER_USER"){
            currentUserUid = RECEIVER_UID
            aboutMeTextView.text = "About"
            logoutBtn.visibility = View.GONE
            editAboutIcon.visibility = View.GONE
            editContactIcon.visibility = View.GONE
            showLocationSection.visibility = View.GONE
        }
        if (currentUserUid != null) {
            mDbRef.child("user").child(currentUserUid).get().addOnSuccessListener { snapshot ->
                val currentUser = snapshot.getValue(User::class.java)
                nameOfUser.setText(currentUser?.name)
                emailid.setText(currentUser?.email)
                if (currentUser?.showLocation == true)
                    showLocationToggleBtn.isChecked = true
                else
                    showLocationToggleBtn.isChecked = false
            }.addOnFailureListener { exception ->
                // Handle any potential errors here
            }
        }

        showLocationToggleBtn.setOnCheckedChangeListener { _, isChecked ->
            currentUserUid?.let { uid ->
                mDbRef.child("user").child(uid).child("showLocation").setValue(isChecked)
            }
        }


        logoutBtn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@UserProfileScreen,EntryActivity::class.java)
            finish()
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}