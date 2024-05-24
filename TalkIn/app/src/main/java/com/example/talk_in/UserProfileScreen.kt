package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.talk_in.databinding.ActivityUserProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserProfileScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileScreenBinding
    private var USER_MODE: String? = null
    private var RECEIVER_UID: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        USER_MODE = intent.getStringExtra("MODE").toString()

        if (USER_MODE == "RECEIVER_USER") {
            RECEIVER_UID = intent.getStringExtra("RECEIVER_UID").toString()
            binding.aboutMeTextView.text = "About"
            binding.logoutBtn.visibility = View.GONE
            binding.editAboutIcon.visibility = View.GONE
            binding.editContactIcon.visibility = View.GONE
            binding.showLocationSection.visibility = View.GONE
        }

        val currentUserUid = mAuth.currentUser?.uid
        currentUserUid?.let { uid ->
            mDbRef.child("user").child(uid).get().addOnSuccessListener { snapshot ->
                val currentUser = snapshot.getValue(User::class.java)
                currentUser?.let {
                    binding.nameOfUser.text = it.name
                    binding.emailid.text = it.email
                    binding.showLocationToggleBtn.isChecked = it.showLocation!!
                }
            }.addOnFailureListener { exception ->
                // Handle any potential errors here
            }
        }

        binding.showLocationToggleBtn.setOnCheckedChangeListener { _, isChecked ->
            currentUserUid?.let { uid ->
                mDbRef.child("user").child(uid).child("showLocation").setValue(isChecked)
            }
        }

        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@UserProfileScreen, EntryActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}
