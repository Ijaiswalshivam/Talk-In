package com.example.talk_in

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EntryActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView // Declare videoView as a property
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser != null){
            val intent = Intent(this@EntryActivity,MainActivity::class.java)
            finish()
            startActivity(intent)
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
        }
        //signup button
        val signUpBtn: Button = findViewById(R.id.signupBtn)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
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
