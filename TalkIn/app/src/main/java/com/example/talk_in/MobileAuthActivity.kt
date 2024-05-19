package com.example.talk_in

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.android.material.textfield.TextInputEditText
import java.util.concurrent.TimeUnit
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MobileAuthActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var signupEmail: Button
    private lateinit var edt_name: com.google.android.material.textfield.TextInputEditText
    private lateinit var edt_phone: com.google.android.material.textfield.TextInputEditText
    private lateinit var edt_otp: com.google.android.material.textfield.TextInputEditText
    private lateinit var otpTextInputLayout: com.google.android.material.textfield.TextInputLayout
    private lateinit var progressSignUp: ProgressBar
    private lateinit var mDbref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnOtp: Button
    private var id: String? = null
    private var isSubmit = false
    private var isVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_auth)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        btnBack = findViewById(R.id.btnBack)
        signupEmail = findViewById(R.id.signupEmail)
        edt_name = findViewById(R.id.edt_name)
        edt_phone = findViewById(R.id.edt_phone)
        edt_otp = findViewById(R.id.edt_otp)
        btnOtp = findViewById(R.id.btnOtp)
        otpTextInputLayout = findViewById(R.id.otpTextInputLayout)
        progressSignUp = findViewById(R.id.progressSignUp)

        btnOtp.setOnClickListener {
//            Log.d("SignUp page Debug", "Sign Up button clicked")
            if (TextUtils.isEmpty(edt_name.text.toString()) || TextUtils.isEmpty(edt_phone.text.toString())){
                if(TextUtils.isEmpty(edt_name.text.toString()))
                    edt_name.setError("Enter Name")
                if(TextUtils.isEmpty(edt_phone.text.toString()))
                    edt_phone.setError("Enter Phone number")
            }
            else{
                checkMobileNumberExists("+91${edt_phone.text.toString()}") { isExists ->
                    if (isExists) {
//                        Log.d("SignUp page Debug", "Mobile Number already registered")
                        Toast.makeText(this@MobileAuthActivity, "Mobile Number already registered", Toast.LENGTH_SHORT).show()
                    } else {
//                        Log.d("SignUp page Debug", "Mobile Number not registered")
                        verifyAndSubmit(it)
                    }
                }
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this@MobileAuthActivity, EntryActivity::class.java)
            finish()
            startActivity(intent)
        }

        signupEmail.setOnClickListener {
            val intent = Intent(this@MobileAuthActivity, SignUp::class.java)
            finish()
            startActivity(intent)
        }
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @SuppressLint("SetTextI18n")
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            Log.d("SignUp page Debug", "onVerificationCompleted: $credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
//            Log.e("SignUp page Debug", "onVerificationFailed", e)
            progressSignUp.visibility = View.GONE
            edt_otp.setText(
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Failed!"
                    is FirebaseTooManyRequestsException -> "Message Quota Exceeded!\nTry Again After few Hours!"
                    else -> "Verification Failed!"
                }
            )
            edt_phone.isEnabled = true
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
//            Log.d("SignUp page Debug", "onCodeSent: $verificationId")
            otpTextInputLayout.hint = "Enter OTP"
            btnOtp.text = "Sign Up"
            id = verificationId
            isSubmit = true
            edt_otp.isEnabled = true
            progressSignUp.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    Log.d("SignUp page Debug", "signInWithCredential:success")
                    val user = task.result?.user

                    // Update UI with the signed-in user's information
                    addUserToDatabase(edt_name.text.toString(), null, "+91${edt_phone.text.toString()}", mAuth.currentUser?.uid!!)

                    progressSignUp.visibility = View.GONE
                    Toast.makeText(this@MobileAuthActivity, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MobileAuthActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
//                    Log.w("SignUp page Debug", "signInWithCredential:failure", task.exception)
                    progressSignUp.visibility = View.GONE
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        edt_otp.setError("Invalid code.")
                    }
                }
            }
    }

    fun verifyAndSubmit(view: View) {
        edt_phone.isEnabled = false
        edt_name.isEnabled = false
        if (!isSubmit) {
            if (!isVerified && edt_phone.text.toString().isNotEmpty() && edt_name.text.toString().isNotEmpty()) {
                val phoneNumber = "+91${edt_phone.text.toString()}"
                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this@MobileAuthActivity)
                    .setCallbacks(mCallbacks)
                    .build()
//                Log.d("SignUp page Debug", "Starting phone number verification for $phoneNumber")
                PhoneAuthProvider.verifyPhoneNumber(options)
                otpTextInputLayout.hint = "Verifying..."
                progressSignUp.visibility = View.VISIBLE
            }
        } else {
            id?.let {
                val otp = edt_otp.text.toString()
                val credential = PhoneAuthProvider.getCredential(it, otp)
                progressSignUp.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun checkMobileNumberExists(mobileNumber: String, callback: (Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        val query = usersRef.orderByChild("mobile").equalTo(mobileNumber)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isExists = dataSnapshot.exists()
                callback(isExists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                callback(false)
            }
        })
    }

    private fun addUserToDatabase(name: String, email: String?, mobile: String, uid: String){
        mDbref = FirebaseDatabase.getInstance().getReference()

        mDbref.child("user").child(uid).setValue(User(name,email, mobile,uid))
    }
}