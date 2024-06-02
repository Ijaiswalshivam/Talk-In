package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.talk_in.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {

  private lateinit var binding: ActivitySignUpBinding
  private lateinit var mAuth: FirebaseAuth
  private lateinit var mDbref: DatabaseReference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySignUpBinding.inflate(layoutInflater)
    setContentView(binding.root)
    supportActionBar?.hide()

    mAuth = FirebaseAuth.getInstance()

    binding.apply {
      btnSignup.setOnClickListener {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val username = edtName.text.toString().trim()
        val talkinid = edttalkinId.text.toString().trim()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(talkinid)) {
          if (TextUtils.isEmpty(username)) edtName.error = "Enter Name"
          if (TextUtils.isEmpty(email)) edtEmail.error = "Enter Email"
          if (TextUtils.isEmpty(password)) edtPassword.error = "Enter Password"
          if (TextUtils.isEmpty(talkinid)) edttalkinId.error = "Enter TalkIn ID"
        } else {
          progressSignUp.visibility = View.VISIBLE
          checkTalkinIdExists(talkinid) { exists ->
            if (exists) {
              progressSignUp.visibility = View.GONE
              edttalkinId.error = "TalkIn ID already exists. Please choose another."
            } else {
              signUp(username, email, password, talkinid)
            }
          }
        }
      }

      loginMobile.setOnClickListener {
        val intent = Intent(this@SignUp, MobileAuthActivity::class.java)
        startActivity(intent)
      }

      btnBack.setOnClickListener {
        startActivity(Intent(this@SignUp, EntryActivity::class.java))
        finish()
      }
    }
  }

  private fun signUp(name: String, email: String, password: String, talkinid: String) {
    binding.progressSignUp.visibility = View.VISIBLE
    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          addUserToDatabase(name, email, null, mAuth.currentUser?.uid!!, talkinid)
          sendVerificationEmail()
          binding.progressSignUp.visibility = View.GONE
          val intent = Intent(this@SignUp, LogIn::class.java)
          finish()
          startActivity(intent)
        } else {
          binding.progressSignUp.visibility = View.GONE
          Toast.makeText(this@SignUp, "Please Try Again, Some Error Occurred", Toast.LENGTH_SHORT).show()
        }
      }
  }

  private fun addUserToDatabase(name: String, email: String?, mobile: String?, uid: String, talkinid: String) {
    mDbref = FirebaseDatabase.getInstance().getReference()
    mDbref.child("user").child(uid).setValue(User(talkinid, name, email, mobile, false, "Hey There! I am using Talk-In", false, uid))
  }

  private fun checkTalkinIdExists(talkinid: String, callback: (Boolean) -> Unit) {
    mDbref = FirebaseDatabase.getInstance().getReference("user")
    val query = mDbref.orderByChild("talkinid").equalTo(talkinid)
    query.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        callback(snapshot.exists())
      }

      override fun onCancelled(error: DatabaseError) {
        callback(false)
      }
    })
  }

  private fun sendVerificationEmail() {
    mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        binding.progressSignUp.visibility = View.GONE
        Toast.makeText(this, "Verification email sent to your email id.", Toast.LENGTH_SHORT).show()
      } else {
        binding.progressSignUp.visibility = View.GONE
        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
