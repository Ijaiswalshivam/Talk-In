package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.talk_in.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class LogIn : AppCompatActivity() {
  private lateinit var mAuth: FirebaseAuth
  private lateinit var mDbRef: DatabaseReference
  private lateinit var binding: ActivityLogInBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLogInBinding.inflate(layoutInflater)
    setContentView(binding.root)

    mAuth = FirebaseAuth.getInstance()
    mDbRef = FirebaseDatabase.getInstance().reference
    supportActionBar?.hide()

    binding.loginMobile.setOnClickListener {
      val i = Intent(this@LogIn, MobileAuthActivity::class.java)
      startActivity(i)
    }

    binding.btnLogin.setOnClickListener {
      val email = binding.edtEmail.text.toString()
      val password = binding.edtPassword.text.toString()
      if (email.isBlank() || password.isBlank()) {
        Toast.makeText(this, "Please enter details.", Toast.LENGTH_SHORT).show()
      } else {
        binding.progressLogin.visibility = View.VISIBLE
        login(email, password)
      }
    }

    binding.btnBack.setOnClickListener {
      val intent = Intent(this@LogIn, EntryActivity::class.java)
      finish()
      startActivity(intent)
    }

    binding.forgetPassword.setOnClickListener {
      val intent = Intent(this@LogIn, ResetPasswordActivity::class.java)
      startActivity(intent)
    }
  }

  private fun login(email: String, pwd: String) {
    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
      mAuth.signInWithEmailAndPassword(email, pwd)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            if (mAuth.currentUser?.isEmailVerified == true) {
              FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                if (tokenTask.isSuccessful) {
                  val deviceToken = tokenTask.result
                  handleDeviceToken(deviceToken, email)
                } else {
                  binding.progressLogin.visibility = View.GONE
                  Toast.makeText(this, "Failed to get device token: ${tokenTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
              }
            } else {
              binding.progressLogin.visibility = View.GONE
              Toast.makeText(this, "Please verify your email id.", Toast.LENGTH_SHORT).show()
            }
          }
        }.addOnFailureListener { e ->
          binding.progressLogin.visibility = View.GONE
          when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
              binding.edtPassword.error = "Invalid Password"
              binding.edtPassword.requestFocus()
            }
            is FirebaseAuthInvalidUserException -> {
              binding.edtEmail.error = "Email Not Registered"
              binding.edtEmail.requestFocus()
            }
            else -> {
              Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
          }
        }
    } else {
      binding.progressLogin.visibility = View.GONE
      Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show()
    }
  }

  private fun handleDeviceToken(deviceToken: String, email: String) {
    val usersRef = mDbRef.child("users-device-tokens")
    usersRef.orderByChild("deviceToken").equalTo(deviceToken).addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
          for (userSnapshot in dataSnapshot.children) {
            val storedEmail = userSnapshot.child("email").getValue(String::class.java)
            if (storedEmail == email) {
              saveDeviceToken(mAuth.currentUser?.uid!!, deviceToken)
              navigateToMainActivity()
            } else {
              binding.progressLogin.visibility = View.GONE
              Toast.makeText(this@LogIn, "Another user is already logged in with this device!", Toast.LENGTH_SHORT).show()
            }
          }
        } else {
          saveDeviceToken(mAuth.currentUser?.uid!!, deviceToken)
          navigateToMainActivity()
        }
      }

      override fun onCancelled(databaseError: DatabaseError) {
        binding.progressLogin.visibility = View.GONE
        Toast.makeText(this@LogIn, "Failed to check device token: ${databaseError.message}", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun saveDeviceToken(userId: String, deviceToken: String) {
    val userData = hashMapOf(
      "userId" to userId,
      "email" to mAuth.currentUser?.email,
      "deviceToken" to deviceToken
    )

    mDbRef.child("users-device-tokens").child(userId).setValue(userData)
      .addOnSuccessListener {
        // Device token saved successfully
      }
      .addOnFailureListener { e ->
        Toast.makeText(this, "Failed to save device token: ${e.message}", Toast.LENGTH_SHORT).show()
      }
  }

  private fun navigateToMainActivity() {
    binding.progressLogin.visibility = View.GONE
    val intent = Intent(this@LogIn, MainActivity::class.java)
    startActivity(intent)
    finish()
    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
  }
}
