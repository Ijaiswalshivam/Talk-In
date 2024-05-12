package com.example.talk_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LogIn : AppCompatActivity() {
  private lateinit var mAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log_in)
    mAuth = FirebaseAuth.getInstance()
    supportActionBar?.hide()

    val edtEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edt_email)
    val edtPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edt_password)
    val btnLogIn = findViewById<Button>(R.id.btnLogin)
    val backbtn = findViewById<ImageView>(R.id.btnBack)

    btnLogIn.setOnClickListener {
      val email = edtEmail.text.toString()
      val password = edtPassword.text.toString()
      if (email.isBlank() || password.isBlank()) {
        Toast.makeText(this, "Please enter details.", Toast.LENGTH_SHORT).show()
      } else
        login(email, password)
    }
    backbtn.setOnClickListener{
      val intent = Intent(this@LogIn,EntryActivity::class.java)
      startActivity(intent)
      finish()
    }



  }

  private fun login(email: String, password: String) {
    //login for logging user

    try {
      mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
          if (task.isSuccessful) {
            // code for logging

            val intent = Intent(this@LogIn, MainActivity::class.java)
            finish()
            startActivity(intent)


          } else {
            Toast.makeText(
              this@LogIn,
              "User doesn't exist..! Please Sign-Up..",
              Toast.LENGTH_SHORT
            ).show()
          }
        }
    } catch (e: Exception) {
      Log.e("#", e.message.toString())
    }

  }

}