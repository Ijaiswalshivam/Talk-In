package com.example.talk_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MobileAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_auth)
        supportActionBar?.hide()
    }
}