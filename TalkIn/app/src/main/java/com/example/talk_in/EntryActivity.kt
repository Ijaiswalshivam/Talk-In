package com.example.talk_in

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class   EntryActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView // Declare videoView as a property
    private lateinit var mAuth: FirebaseAuth
    private lateinit var google_icon: ImageView
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mDbref: DatabaseReference
    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "EntryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        google_icon = findViewById(R.id.google_icon)

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser != null && mAuth.currentUser!!.isEmailVerified){
            val intent = Intent(this@EntryActivity,MainActivity::class.java)
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        }

        //hide actionbar
        supportActionBar?.hide()
        //video play
        videoView = findViewById(R.id.videoView) // Initialize videoView
        val uri = Uri.parse("android.resource://$packageName/${R.raw.bg}")
        videoView.setVideoURI(uri)
        videoView.setOnCompletionListener {
            videoView.start()
        }
        videoView.start()
        //signin button
        val signInBtn: Button = findViewById(R.id.signinBtn)
        signInBtn.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        }
        //signup button
        val signUpBtn: Button = findViewById(R.id.signupBtn)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_icon.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    addUserToDatabase(user?.displayName.toString(), user?.email.toString(), null, user?.uid.toString())
                    if (user != null && user.isEmailVerified) {
                        val intent = Intent(this@EntryActivity, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                    } else {
                        Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EntryActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun addUserToDatabase(name: String, email: String, mobile: String?, uid: String){
        mDbref = FirebaseDatabase.getInstance().getReference()

        mDbref.child("user").child(uid).setValue(User(name,email, null, false, uid))
    }
    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }
}