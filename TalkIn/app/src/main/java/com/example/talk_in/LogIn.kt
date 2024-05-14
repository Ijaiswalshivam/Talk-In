package com.example.talk_in

import android.content.Intent
<<<<<<< HEAD
=======
import android.os.Bundle
import android.text.TextUtils
>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
<<<<<<< HEAD
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
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogIn = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignup)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        btnLogIn.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email, password)
        }
=======
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f


class LogIn : AppCompatActivity() {
  private lateinit var mAuth: FirebaseAuth
  private lateinit var edtEmail : com.google.android.material.textfield.TextInputEditText
  private lateinit var edtPassword : com.google.android.material.textfield.TextInputEditText
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log_in)
    mAuth = FirebaseAuth.getInstance()
    supportActionBar?.hide()

    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    val btnLogIn = findViewById<Button>(R.id.btnLogin)
    val backbtn = findViewById<ImageView>(R.id.btnBack)
    val forgetPassword = findViewById<TextView>(R.id.forgetPassword)

    btnLogIn.setOnClickListener {
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()
      if (email.isBlank() || password.isBlank()) {
        Toast.makeText(this, "Please enter details.", Toast.LENGTH_SHORT).show()
      } else{
        login(email, password)
      }
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

<<<<<<< HEAD
     private fun login(email: String, password: String) {
         mAuth.signInWithEmailAndPassword(email, password)
             .addOnCompleteListener(this) { task ->
                 if (task.isSuccessful) {
                     val user = mAuth.currentUser
                     val username = user?.displayName

                     FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                         if (tokenTask.isSuccessful) {
                             val deviceToken = tokenTask.result

                             // Check if device token already exists in the database
                             val usersRef = Firebase.database.reference.child("users-device-tokens")
                             usersRef.orderByChild("deviceToken").equalTo(deviceToken).addListenerForSingleValueEvent(object :
                                 ValueEventListener {
                                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                                     if (dataSnapshot.exists()) {
                                         // Device token exists in the database
                                         var isTokenAssociatedWithSameEmail = false
                                         for (userSnapshot in dataSnapshot.children) {
                                             val storedEmail = userSnapshot.child("email").getValue(String::class.java)
                                             if (storedEmail == email) {
                                                 // Device token is associated with the same email trying to log in
                                                 isTokenAssociatedWithSameEmail = true
                                                 break
                                             }
                                         }

                                         if (isTokenAssociatedWithSameEmail) {

                                             saveDeviceToken(user?.uid ?: "", username, email, deviceToken)
                                         } else {
                                             Toast.makeText(this@LogIn, "Another user is already logged in with this device!", Toast.LENGTH_SHORT).show()
                                         }
                                     } else {

                                         saveDeviceToken(user?.uid ?: "", username, email, deviceToken)
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
=======

  }

    private fun login(email: String, pwd: String) {
      // CHecking for empty texts
      if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
        mAuth.signInWithEmailAndPassword(email, pwd)
          .addOnCompleteListener { task ->
            if (task.isSuccessful) {
              if (isEmailVerified()) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LogIn, MainActivity::class.java)
                startActivity(intent)
                finish()
              }
            }
          }.addOnFailureListener { e ->
            if (e is FirebaseAuthInvalidCredentialsException) {
              edtPassword.error = "Invalid Password"
              edtPassword.requestFocus()
            }
            if (e is FirebaseAuthInvalidUserException) {
              edtEmail.error = "Email Not Registered"
              edtEmail.requestFocus()
            } else {
              Toast.makeText(
                this,
                "Something went Wrong",
                Toast.LENGTH_SHORT
              ).show()
            }
          }
      } else {
        Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show()
      }
    }
  private fun isEmailVerified(): Boolean {
    if (mAuth.currentUser != null) {
      val isEmailVerified: Boolean = mAuth.currentUser!!.isEmailVerified
      if (isEmailVerified) {
        return true
      } else {
        Toast.makeText(this, "please verify your email address first", Toast.LENGTH_SHORT).show()
      }
    }
    return false
  }

}
>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f
