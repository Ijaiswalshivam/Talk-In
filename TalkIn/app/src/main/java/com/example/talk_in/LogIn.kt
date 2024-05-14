package com.example.talk_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessaging

class LogIn : AppCompatActivity() {
  private lateinit var edtEmail: EditText
  private lateinit var edtPassword: EditText
  private lateinit var btnLogIn: Button
  private lateinit var mAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log_in)
    mAuth = FirebaseAuth.getInstance()

    supportActionBar?.hide()

    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    val backbtn = findViewById<ImageView>(R.id.btnBack)
    btnLogIn = findViewById(R.id.btnLogin)
    val forgetPassword = findViewById<TextView>(R.id.forgetPassword)

    btnLogIn.setOnClickListener {
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()

      login(email, password);
    }
    backbtn.setOnClickListener{
      val intent = Intent(this@LogIn,EntryActivity::class.java)
      startActivity(intent)
      finish()
    }

    forgetPassword.setOnClickListener {
      val intent = Intent(this@LogIn, ResetPasswordActivity::class.java)
      startActivity(intent)
      finish()
    }
  }

  private fun login(email: String, password: String) {
    if (email.isEmpty() || !email.contains("@")) {
      Toast.makeText(this@LogIn, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
      return
    }

    if (password.length < 6) {
      Toast.makeText(this@LogIn, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
      return
    }

    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          val user = mAuth.currentUser
          val username = user?.displayName

          // Retrieve device token
          FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
              val deviceToken = tokenTask.result

              // Check if device token already exists in the database
              val usersRef = Firebase.database.reference.child("users-device-tokens")
              usersRef.orderByChild("deviceToken").equalTo(deviceToken).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                  if (dataSnapshot.exists()) {
                    // Device token is associated with one or more users
                    for (userSnapshot in dataSnapshot.children) {
                      val storedEmail = userSnapshot.child("email").getValue(String::class.java)
                      if (storedEmail == email) {
                        // Device token is associated with the same email trying to log in Proceed with login
                        if (user != null) {
                          saveDeviceToken(user.uid, username, email, deviceToken)
                        }
                        return
                      } else {
                        // Device token is associated with a different email
                        Toast.makeText(this@LogIn, "Another user is already logged in with this device!", Toast.LENGTH_SHORT).show()
                        return
                      }
                    }
                  } else {
                    // Device token is not present in the database Proceed with login
                    if (user != null) {
                      saveDeviceToken(user.uid, username, email, deviceToken)
                    }
                  }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                  Toast.makeText(this@LogIn, "Failed to check device token: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
              })
            } else {
              Toast.makeText(this@LogIn, "Failed to get device token: ${tokenTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
          }
        } else {
          Toast.makeText(this@LogIn, "User doesn't exist..! Please Sign-Up..", Toast.LENGTH_SHORT).show()
        }
      }
  }

  private fun saveDeviceToken(userId: String, username: String?, email: String, deviceToken: String?) {
    val userData = hashMapOf(
      "userId" to userId,
      "username" to username,
      "email" to email,
      "deviceToken" to deviceToken
    )

    val usersRef = Firebase.database.reference.child("users-device-tokens").child(userId)
    usersRef.setValue(userData)
      .addOnSuccessListener {
        navigateToMainActivity()
        Toast.makeText(this@LogIn, "Login Successful..!", Toast.LENGTH_SHORT).show()
      }
      .addOnFailureListener { e ->
        Toast.makeText(this@LogIn, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
      }
  }

  private fun navigateToMainActivity() {
    val intent = Intent(this@LogIn, MainActivity::class.java)
    startActivity(intent)
    finish()
  }

}
