package com.example.talk_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)

        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener{
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(name,email,password)
        }
    }

    private fun signUp(name:String, email: String, password: String){
        // Attempt to create a new user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, add user to database
                        val uid = mAuth.currentUser?.uid
                        if (uid != null) {
                            addUserToDatabase(name, email, uid)
                        }

                        // Navigate to MainActivity
                        val intent= Intent(this@SignUp, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finish the current activity
                    } else {
                        // Registration failed, display an error message
                        Toast.makeText(this@SignUp, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener(this) { exception ->
                    // Handle any exceptions that occur during registration
                    Log.e("SignUpActivity", "Registration error: ${exception.message}", exception)
                    Toast.makeText(this@SignUp, "An unexpected error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
                }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String){
        // Add user details to Firebase Realtime Database
        mDbref = FirebaseDatabase.getInstance().getReference("users")
        val user = User(name, email, uid)
        mDbref.child(uid).setValue(user)
    }
}
