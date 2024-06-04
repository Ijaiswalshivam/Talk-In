package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.talk_in.databinding.ActivityCreateGroupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding: ActivityCreateGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupMembers: ArrayList<String>? = intent.getStringArrayListExtra("GROUP_MEMBERS")
        binding.confirmButton.setOnClickListener {
            val groupName = binding.groupNameTextfield.text.toString().trim()
            var groupDescription = binding.groupDescriptionField.text.toString().trim()

            if (TextUtils.isEmpty(groupName))
                Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show()
            else{
                if (TextUtils.isEmpty(groupDescription))
                    groupDescription = "Hey, Welcome to " + groupName
                createGroup(groupName, groupDescription, groupMembers!!)
            }
        }
    }
    private fun createGroup(groupName: String, groupDescription: String?, groupMembers: ArrayList<String>) {
        mDbRef = FirebaseDatabase.getInstance().reference
        val groupId = mDbRef.child("groups").push().key ?: ""

        val group = Group(groupName, groupDescription, groupMembers, groupId)

        mDbRef.child("groups").child(groupId).setValue(group)
            .addOnSuccessListener {
                Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CreateGroupActivity, GroupChatActivity::class.java)
                intent.putExtra("NAME", groupName)
                intent.putExtra("GROUP_ID", groupId)

                finish()
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Log.e("CreateGroupActivity", "Error creating group", exception)
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CreateGroupActivity, Groups::class.java)
                finish()
                startActivity(intent)
            }
    }
}