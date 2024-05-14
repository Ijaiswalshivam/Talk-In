package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

  private lateinit var edtName: com.google.android.material.textfield.TextInputEditText
  private lateinit var edtEmail: com.google.android.material.textfield.TextInputEditText
  private lateinit var edtPassword: com.google.android.material.textfield.TextInputEditText
  private lateinit var btnSignUp: Button
  private lateinit var mAuth: FirebaseAuth
  private val db = FirebaseFirestore.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    supportActionBar?.hide()

    mAuth = FirebaseAuth.getInstance()

    edtName = findViewById(R.id.edt_name)
    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    btnSignUp = findViewById(R.id.btnSignup)
    val backbtn = findViewById<ImageView>(R.id.btnBack)

    backbtn.setOnClickListener {
      startActivity(Intent(this@SignUp, EntryActivity::class.java))
      finish()
    }

    btnSignUp.setOnClickListener {
      val email = edtEmail.text.toString().trim()
      val password = edtPassword.text.toString().trim()
      val username = edtName.text.toString().trim()

      if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
        Toast.makeText(this@SignUp, "Enter Details", Toast.LENGTH_SHORT).show()
      } else {
        mAuth.createUserWithEmailAndPassword(email, password)
<<<<<<< HEAD
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //for verification of email address


                    //code for jumping home activity
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent= Intent(this@SignUp,LogIn::class.java)
                    finish()
                    startActivity(intent)
                    Toast.makeText(this@SignUp, "Sign Up Successful.. Now Login..!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignUp,"Please Try Again,Some Error Occurred",Toast.LENGTH_SHORT).show()
=======
          .addOnCompleteListener { task ->
            if (task.isSuccessful) {
              val currentUserId = mAuth.currentUser?.uid ?: ""
              val userObj = hashMapOf("email" to email, "username" to username)
              db.collection("User").document(currentUserId).set(userObj)
                .addOnSuccessListener {
                  Toast.makeText(this@SignUp, "Registration Successful", Toast.LENGTH_SHORT).show()
                  sendVerificationEmail()
                  val intent = Intent(this@SignUp,LogIn::class.java)
                  startActivity(intent)
                  finish()
>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f
                }
                .addOnFailureListener { e ->
                  Toast.makeText(this@SignUp, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
              Toast.makeText(this@SignUp, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
          }
      }
    }
  }

  private fun sendVerificationEmail() {
    mAuth.currentUser?.sendEmailVerification()
      ?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Toast.makeText(this@SignUp, "Verification email sent", Toast.LENGTH_SHORT).show()
        } else {
          Toast.makeText(this@SignUp, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }
      }
  }
}
