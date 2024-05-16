package com.example.talk_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MobileAuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    private lateinit var otpDigit1: EditText
    private lateinit var otpDigit2: EditText
    private lateinit var otpDigit3: EditText
    private lateinit var otpDigit4: EditText
    private lateinit var otpDigit5: EditText
    private lateinit var otpDigit6: EditText
    private lateinit var mobileNumberInputLayout: TextInputLayout
    private lateinit var mobileNumberEditText: TextInputEditText
    private lateinit var getOtpButton: Button
    private lateinit var verifyOtpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_auth)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        mobileNumberInputLayout = findViewById(R.id.mobileNumberInputLayout)
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText)
        getOtpButton = findViewById(R.id.getOtpButton)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)
        //resendOtpButton = findViewById(R.id.resendOtpButton)

        otpDigit1 = findViewById(R.id.otpDigit1)
        otpDigit2 = findViewById(R.id.otpDigit2)
        otpDigit3 = findViewById(R.id.otpDigit3)
        otpDigit4 = findViewById(R.id.otpDigit4)
        otpDigit5 = findViewById(R.id.otpDigit5)
        otpDigit6 = findViewById(R.id.otpDigit6)
        val backbtn = findViewById<ImageView>(R.id.btnBack)
        backbtn.setOnClickListener{
            val intent = Intent(this@MobileAuthActivity,EntryActivity::class.java)
            startActivity(intent)
            finish()
        }
        getOtpButton.setOnClickListener {
            val phoneNumber = mobileNumberEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            } else {
                mobileNumberInputLayout.error = "Please enter a valid mobile number"
            }
        }

        verifyOtpButton.setOnClickListener {
            val otp = otpDigit1.text.toString() + otpDigit2.text.toString() +
                    otpDigit3.text.toString() + otpDigit4.text.toString() +
                    otpDigit5.text.toString() + otpDigit6.text.toString()
            if (otp.isNotEmpty() && otp.length == 6) {
                verifyCode(otp)
            } else {
                Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verifies the code
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Verification failed
                    Toast.makeText(this@MobileAuthActivity, e.message, Toast.LENGTH_LONG).show()
                    Log.e("MainActivity", "Verification failed: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Save verification id to verify later
                    this@MobileAuthActivity.verificationId = verificationId
                    Toast.makeText(this@MobileAuthActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Verification Successful", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed
                    Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}