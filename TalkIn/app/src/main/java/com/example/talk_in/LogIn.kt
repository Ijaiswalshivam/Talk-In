package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
  private lateinit var edtEmail: com.google.android.material.textfield.TextInputEditText
  private lateinit var edtPassword: com.google.android.material.textfield.TextInputEditText
  private lateinit var mDbRef: DatabaseReference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log_in)
    mAuth = FirebaseAuth.getInstance()
    mDbRef = FirebaseDatabase.getInstance().reference
    supportActionBar?.hide()

    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    val progressLogin: ProgressBar = findViewById(R.id.progressLogin)
    val btnLogIn = findViewById<Button>(R.id.btnLogin)
    val backbtn = findViewById<ImageView>(R.id.btnBack)
    val forgetPassword = findViewById<TextView>(R.id.forgetPassword)
    val loginMobBtn = findViewById<Button>(R.id.loginMobile)

    loginMobBtn.setOnClickListener {
      val i = Intent(this@LogIn, MobileAuthActivity::class.java)
      startActivity(i)
    }

    btnLogIn.setOnClickListener {
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()
      if (email.isBlank() || password.isBlank()) {
        Toast.makeText(this, "Please enter details.", Toast.LENGTH_SHORT).show()
      } else {
        progressLogin.visibility = View.VISIBLE
        login(email, password)
      }
    }
    backbtn.setOnClickListener {
      val intent = Intent(this@LogIn, EntryActivity::class.java)
      startActivity(intent)
      finish()
    }
    forgetPassword.setOnClickListener {
      val intent = Intent(this@LogIn, ResetPasswordActivity::class.java)
      startActivity(intent)
      finish()
    }
  }

  private fun login(email: String, pwd: String) {
    val progressLogin: ProgressBar = findViewById(R.id.progressLogin)
    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
      mAuth.signInWithEmailAndPassword(email, pwd)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            if (mAuth.currentUser?.isEmailVerified == true) {
              FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                if (tokenTask.isSuccessful) {
                  val deviceToken = tokenTask.result
                  handleDeviceToken(deviceToken, email, progressLogin)
                } else {
                  progressLogin.visibility = View.GONE
                  Toast.makeText(this, "Failed to get device token: ${tokenTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
              }
            } else {
              progressLogin.visibility = View.GONE
              Toast.makeText(this, "Please verify your email id.", Toast.LENGTH_SHORT).show()
            }
          }
        }.addOnFailureListener { e ->
          progressLogin.visibility = View.GONE
          when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
              edtPassword.error = "Invalid Password"
              edtPassword.requestFocus()
            }
            is FirebaseAuthInvalidUserException -> {
              edtEmail.error = "Email Not Registered"
              edtEmail.requestFocus()
            }
            else -> {
              Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
          }
        }
    } else {
      progressLogin.visibility = View.GONE
      Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show()
    }
  }

  private fun handleDeviceToken(deviceToken: String, email: String, progressLogin: ProgressBar) {
    val usersRef = mDbRef.child("users-device-tokens")
    usersRef.orderByChild("deviceToken").equalTo(deviceToken).addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
          for (userSnapshot in dataSnapshot.children) {
            val storedEmail = userSnapshot.child("email").getValue(String::class.java)
            if (storedEmail == email) {
              saveDeviceToken(mAuth.currentUser?.uid!!, deviceToken)
              navigateToMainActivity(progressLogin)
            } else {
              progressLogin.visibility = View.GONE
              Toast.makeText(this@LogIn, "Another user is already logged in with this device!", Toast.LENGTH_SHORT).show()
            }
          }
        } else {
          saveDeviceToken(mAuth.currentUser?.uid!!, deviceToken)
          navigateToMainActivity(progressLogin)
        }
      }

      override fun onCancelled(databaseError: DatabaseError) {
        progressLogin.visibility = View.GONE
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

  private fun navigateToMainActivity(progressLogin: ProgressBar) {
    progressLogin.visibility = View.GONE
    val intent = Intent(this@LogIn, MainActivity::class.java)
    startActivity(intent)
    finish()
    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
  }
}
