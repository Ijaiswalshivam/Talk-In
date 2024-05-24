package com.example.talk_in

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.example.talk_in.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailResetPassword
    }

    fun resetPasswordNow(view: View) {
        if (binding.emailResetPassword.text.toString().isEmpty()) {
            binding.emailResetPassword.error = "Fill this field."
        } else {
            val snack = Snackbar.make(findViewById(android.R.id.content), "Password Reset Link Sent On Registered Email.", Snackbar.LENGTH_LONG)
            val view1 = snack.view
            val params = view1.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER_VERTICAL
            view1.layoutParams = params
            FirebaseAuth.getInstance().sendPasswordResetEmail(binding.emailResetPassword.text.toString())
            snack.show()
        }
    }
}
