package com.example.talk_in

import GroupMemberSelectionAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.talk_in.databinding.ActivityGroupMemberSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupMemberSelection : AppCompatActivity() {
    private lateinit var binding: ActivityGroupMemberSelectionBinding
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: GroupMemberSelectionAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMemberSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = GroupMemberSelectionAdapter(this, userList)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (currentUser != null && mAuth.currentUser?.uid != currentUser.uid && currentUser.verified == true) {
                        userList.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })

        binding.confirmButton.setOnClickListener {
            // Get the list of selected users from the adapter
            val selectedUsers = adapter.getSelectedUsers()
            val uidOfUsers = ArrayList<String>()
            for (user in selectedUsers){
                uidOfUsers.add(user.uid.toString())
            }
            val intent = Intent(this@GroupMemberSelection, CreateGroupActivity::class.java)
            intent.putStringArrayListExtra("GROUP_MEMBERS", uidOfUsers)
            finish()
            startActivity(intent)
        }
    }
}