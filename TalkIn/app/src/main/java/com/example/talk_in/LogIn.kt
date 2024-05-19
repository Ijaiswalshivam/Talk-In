package com.example.talk_in

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException




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
    val progressLogin: ProgressBar = findViewById(R.id.progressLogin)
    val btnLogIn = findViewById<Button>(R.id.btnLogin)
    val backbtn = findViewById<ImageView>(R.id.btnBack)
    val forgetPassword = findViewById<TextView>(R.id.forgetPassword)
    val loginMobBtn = findViewById<Button>(R.id.loginMobile)
    loginMobBtn.setOnClickListener {
      val i = Intent(this@LogIn,MobileAuthActivity::class.java)
      startActivity(i)
    }

    btnLogIn.setOnClickListener {
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()
      if (email.isBlank() || password.isBlank()) {
        Toast.makeText(this, "Please enter details.", Toast.LENGTH_SHORT).show()
      } else{
        progressLogin.visibility = View.VISIBLE
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


  }

  private fun login(email: String, pwd: String) {
    val progressLogin: ProgressBar = findViewById(R.id.progressLogin)
    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
      mAuth.signInWithEmailAndPassword(email, pwd)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            if (mAuth.currentUser?.isEmailVerified == true) {
              progressLogin.visibility = View.GONE
              val intent = Intent(this@LogIn, MainActivity::class.java)
              Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
              startActivity(intent)
              finish()
            } else {
              progressLogin.visibility = View.GONE
              Toast.makeText(this, "Please verify your email id.", Toast.LENGTH_SHORT).show()
            }
          }
        }.addOnFailureListener { e ->
          when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
              progressLogin.visibility = View.GONE
              edtPassword.error = "Invalid Password"
              edtPassword.requestFocus()
            }
            is FirebaseAuthInvalidUserException -> {
              progressLogin.visibility = View.GONE
              edtEmail.error = "Email Not Registered"
              edtEmail.requestFocus()
            }
            else -> {
              progressLogin.visibility = View.GONE
              Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
          }
        }
    } else {
      progressLogin.visibility = View.GONE
      Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show()
    }
  }
}