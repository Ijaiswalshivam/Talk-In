package com.example.talk_in

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.example.talk_in.databinding.ActivityUserProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException


class UserProfileScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileScreenBinding
    private var USER_MODE: String? = null
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        USER_MODE = intent.getStringExtra("MODE").toString()

        var currentUserUid = mAuth.currentUser?.uid.toString()
        if (USER_MODE == "RECEIVER_USER") {
            currentUserUid = intent.getStringExtra("RECEIVER_UID").toString()
            binding.aboutMeTextViewTitle.text = "About"
            binding.logoutBtn.visibility = View.GONE
            binding.editAboutIcon.visibility = View.GONE
            binding.editContactIcon.visibility = View.GONE
            binding.showLocationSection.visibility = View.GONE
            binding.userprofileImageBtn.visibility = View.GONE
        }

        setProfileImage(currentUserUid)

        currentUserUid?.let { uid ->
            mDbRef.child("user").child(uid).get().addOnSuccessListener { snapshot ->
                val currentUser = snapshot.getValue(User::class.java)
                currentUser?.let {
                    binding.nameOfUser.text = it.name
                    binding.emailid.text = it.email
                    binding.showLocationToggleBtn.isChecked = it.showLocation!!
                    binding.aboutMeTextView.text = it.aboutMe
                }
            }.addOnFailureListener { exception ->
                // Handle any potential errors here
            }
        }

        binding.showLocationToggleBtn.setOnCheckedChangeListener { _, isChecked ->
            currentUserUid?.let { uid ->
                mDbRef.child("user").child(uid).child("showLocation").setValue(isChecked)
            }
        }

        binding.userprofileImageBtn.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 100)
        }

        binding.logoutBtn.setOnClickListener {
            val currentUser = mAuth.currentUser
            currentUser?.uid?.let { userId ->
                mDbRef.child("users-device-tokens").child(userId).removeValue()
                        .addOnSuccessListener {
                            mAuth.signOut()
                            val intent = Intent(this@UserProfileScreen, LogIn::class.java)
                            finish()
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                        }
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.editAboutIcon.setOnClickListener {
            // Call the function passing the activity context and a lambda for handling the result
            showCustomDialog(this) { newText ->
                // Handle the result (newText) here
                Toast.makeText(this, "New text: $newText", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun showCustomDialog(context: Context, listener: (String) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_text)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Edit Status")
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()
                listener.invoke(newText)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadImageToFirebaseStorage(imageUri)
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val currentUserUid = mAuth.currentUser?.uid ?: return
        storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images")
            .child("$currentUserUid.jpg")
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    mDbRef.child("user").child(currentUserUid).child("profileImageUrl")
                        .setValue(uri.toString())
                    setProfileImage(currentUserUid)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that may occur during the upload process
            }
    }

    private fun setProfileImage(currentUserUid: String) {
        storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images")
            .child("$currentUserUid.jpg")
        try {
            val localFile = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(resources, bitmap)
                    circularBitmapDrawable.isCircular = true
                    binding.userprofileImage.setImageDrawable(circularBitmapDrawable)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun updateAboutMe(aboutMe: String, currentUserUid: String){
        mDbRef.child("user").child(currentUserUid).child("aboutMe").setValue(aboutMe)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}