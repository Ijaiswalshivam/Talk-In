package com.example.talk_in

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.talk_in.databinding.ActivityMobileAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class MobileAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMobileAuthBinding
    private lateinit var mDbref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var id: String? = null
    private var isSubmit = false
    private var isVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.btnOtp.setOnClickListener {
            if (binding.edtName.text.isNullOrBlank() || binding.edtPhone.text.isNullOrBlank()) {
                if (binding.edtName.text.isNullOrBlank())
                    binding.edtName.error = "Enter Name"
                if (binding.edtPhone.text.isNullOrBlank())
                    binding.edtPhone.error = "Enter Phone number"
            } else {
                checkMobileNumberExists("+91${binding.edtPhone.text.toString()}") { isExists ->
                    if (isExists) {
                        // Mobile number already registered
                        binding.edtPhone.error = "Mobile Number already registered"
                    } else {
                        verifyAndSubmit()
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@MobileAuthActivity, EntryActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.signupEmail.setOnClickListener {
            val intent = Intent(this@MobileAuthActivity, SignUp::class.java)
            finish()
            startActivity(intent)
        }
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @SuppressLint("SetTextI18n")
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.progressSignUp.visibility = View.GONE
            binding.edtOtp.setText(
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Failed!"
                    is FirebaseTooManyRequestsException -> "Message Quota Exceeded!\nTry Again After few Hours!"
                    else -> "Verification Failed!"
                }
            )
            binding.edtPhone.isEnabled = true
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            binding.otpTextInputLayout.hint = "Enter OTP"
            binding.btnOtp.text = "Sign Up"
            id = verificationId
            isSubmit = true
            binding.edtOtp.isEnabled = true
            binding.progressSignUp.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    addUserToDatabase(binding.edtName.text.toString(), null, "+91${binding.edtPhone.text.toString()}", mAuth.currentUser?.uid!!)
                    binding.progressSignUp.visibility = View.GONE
                    val intent = Intent(this@MobileAuthActivity, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    binding.progressSignUp.visibility = View.GONE
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.edtOtp.error = "Invalid code."
                    }
                }
            }
    }

    private fun verifyAndSubmit() {
        binding.edtPhone.isEnabled = false
        binding.edtName.isEnabled = false
        if (!isSubmit) {
            if (!isVerified && !binding.edtPhone.text.isNullOrBlank() && !binding.edtName.text.isNullOrBlank()) {
                val phoneNumber = "+91${binding.edtPhone.text.toString()}"
                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this@MobileAuthActivity)
                    .setCallbacks(mCallbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                binding.otpTextInputLayout.hint = "Verifying..."
                binding.progressSignUp.visibility = View.VISIBLE
            }
        } else {
            id?.let {
                val otp = binding.edtOtp.text.toString()
                val credential = PhoneAuthProvider.getCredential(it, otp)
                binding.progressSignUp.visibility = View.VISIBLE
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
                callback(false)
            }
        })
    }

    private fun addUserToDatabase(name: String, email: String?, mobile: String, uid: String) {
        mDbref = FirebaseDatabase.getInstance().getReference()
        mDbref.child("user").child(uid).setValue(User(name, email, mobile, false, "Hey There! I am using Talk-In", uid))
    }
}
