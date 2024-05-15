package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignUp : AppCompatActivity() {

  private lateinit var edtName: EditText
  private lateinit var edtEmail: EditText
  private lateinit var edtPassword: EditText
  private lateinit var btnSignUp: Button
  private lateinit var mAuth: FirebaseAuth
  private lateinit var mDbRef: DatabaseReference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    supportActionBar?.hide()

    mAuth = FirebaseAuth.getInstance()
    mDbRef = FirebaseDatabase.getInstance().reference

    initializeViews()
    setUpListeners()
  }

  private fun initializeViews() {
    edtName = findViewById(R.id.edt_name)
    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    btnSignUp = findViewById(R.id.btnSignup)
    findViewById<ImageView>(R.id.btnBack).setOnClickListener {
      startActivity(Intent(this@SignUp, EntryActivity::class.java))
      finish()
    }
  }

  private fun setUpListeners() {
    btnSignUp.setOnClickListener {
      val name = edtName.text.toString()
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()

      if (validateInput(name, email, password)) {
        signUp(name, email, password)
      }
    }
  }

  private fun validateInput(name: String, email: String, password: String): Boolean {
    if (name.isEmpty()) {
      showToast("Name field cannot be empty!")
      return false
    }
    if (!email.contains("@")) {
      showToast("Please enter a valid email address!")
      return false
    }
    if (password.length < 6) {
      showToast("Password must be at least 6 characters long!")
      return false
    }
    return true
  }

  private fun signUp(name: String, email: String, password: String) {
    mDbRef.child("user").orderByChild("email").equalTo(email)
      .addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          if (dataSnapshot.exists()) {
            showToast("User already signed up for this email, kindly login!")
          } else {
            mAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(this@SignUp) { task ->
                if (task.isSuccessful) {
                  addUserToDatabase(name, email, mAuth.currentUser?.uid!!)
                  startActivity(Intent(this@SignUp, LogIn::class.java))
                  finish()
                  showToast("SignUp Successful, Now Login..!")
                } else {
                  showToast("Please Try Again, Some Error Occurred")
                }
              }
          }
        }

        override fun onCancelled(databaseError: DatabaseError) {
          showToast("Failed to check email: ${databaseError.message}")
        }
      })
  }

  private fun addUserToDatabase(name: String, email: String, uid: String) {
    mDbRef.child("user").child(uid).setValue(User(name, email, uid))
  }

  private fun showToast(message: String) {
    Toast.makeText(this@SignUp, message, Toast.LENGTH_SHORT).show()
  }
}
