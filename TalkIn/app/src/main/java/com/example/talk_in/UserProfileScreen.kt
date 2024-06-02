package com.example.talk_in

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.talk_in.databinding.ActivityUserProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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

        // Initialize Firebase Auth and Database Reference
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

        currentUserUid.let { uid ->
            mDbRef.child("user").child(uid).get().addOnSuccessListener { snapshot ->
                val currentUser = snapshot.getValue(User::class.java)
                currentUser?.let {
                    binding.nameOfUser.text = it.name
                    binding.emailid.text = it.email
                    binding.showLocationToggleBtn.isChecked = it.showLocation!!
                    binding.aboutMeTextView.text = it.aboutMe
                    if (it.mobile.isNullOrEmpty()) {
                        binding.phoneNumber.text = "Add a new Phone Number"
                    } else {
                        binding.phoneNumber.text = it.mobile
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle any potential errors here
            }
        }

        binding.showLocationToggleBtn.setOnCheckedChangeListener { _, isChecked ->
            currentUserUid.let { uid ->
                mDbRef.child("user").child(uid).child("showLocation").setValue(isChecked)
            }
        }

        binding.userprofileImageBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
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
                        // Handle error
                    }
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.editAboutIcon.setOnClickListener {
            showCustomDialog(this) { newText ->
                // Handle the result (newText) here
                mAuth.currentUser?.let { it1 ->
                    mDbRef.child("user").child(it1.uid).child("aboutMe").setValue(newText)
                        .addOnSuccessListener {
                            binding.aboutMeTextView.text = newText
                        }
                        .addOnFailureListener{
                            Toast.makeText(this@UserProfileScreen, "Failed to update value!!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        binding.editContactIcon.setOnClickListener {
            showPhoneNumberDialog(this) { newPhoneNumber ->
                mAuth.currentUser?.let { currentUser ->
                    val userUid = currentUser.uid
                    mDbRef.child("user").child(userUid).child("mobile").setValue(newPhoneNumber)
                        .addOnSuccessListener {
                            binding.phoneNumber.text = newPhoneNumber
                            Toast.makeText(this@UserProfileScreen, "Phone number updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@UserProfileScreen, "Failed to update phone number!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

    }

    private fun showCustomDialog(context: Context, listener: (String) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_text)

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Edit Status")
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    listener.invoke(newText)
                } else {
                    Toast.makeText(this, "Status cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
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
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.error_profile_image)
                .into(binding.userprofileImage)
        }
    }

    private fun updateAboutMe(aboutMe: String, currentUserUid: String) {
        mDbRef.child("user").child(currentUserUid).child("aboutMe").setValue(aboutMe)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@UserProfileScreen, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun showPhoneNumberDialog(context: Context, listener: (String) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_phone_layout, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_phone_text)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Edit Phone Number")
            .setPositiveButton("Save") { _, _ ->
                val newPhoneNumber = editText.text.toString()
                listener.invoke(newPhoneNumber)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }
}
