package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        val createPassword = createPassword.text.toString().trim()
        val confirmPassword = confirmPassword.text.toString().trim()
        val username = edtName.text.toString().trim()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(createPassword) || TextUtils.isEmpty(confirmPassword)) {
          Toast.makeText(this@SignUp, "Enter Details", Toast.LENGTH_SHORT).show()
        }
        else if (createPassword != confirmPassword) {
          Toast.makeText(this@SignUp, "Passwords are not matching !!", Toast.LENGTH_SHORT).show()
        }
        else {
          progressSignUp.visibility = View.VISIBLE
          signUp(username, email, createPassword)
        }
      }

      loginMobile.setOnClickListener {
        val i = Intent(this@SignUp, MobileAuthActivity::class.java)
        startActivity(i)
      }

      btnBack.setOnClickListener {
        startActivity(Intent(this@SignUp, EntryActivity::class.java))
        finish()
      }
    }
  }

  private fun signUp(name:String, email: String, password: String){
    binding.progressSignUp.visibility = View.VISIBLE
    // Creating user
    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          //code for jumping home activity
          addUserToDatabase(name, email, null, mAuth.currentUser?.uid!!)
          sendVerificationEmail()
          binding.progressSignUp.visibility = View.GONE
          val intent= Intent(this@SignUp, LogIn::class.java)
          finish()
          startActivity(intent)
        } else {
          binding.progressSignUp.visibility = View.GONE
          Toast.makeText(this@SignUp,"Please Try Again,Some Error Occurred",Toast.LENGTH_SHORT).show()
        }
      }
  }

  private fun addUserToDatabase(name: String, email: String, mobile: String?, uid: String){
    mDbref = FirebaseDatabase.getInstance().getReference()
    mDbref.child("user").child(uid).setValue(User(name, email, null, false, "Hey There! I am using Talk-In", false, uid))
  }

  fun sendVerificationEmail() {
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
