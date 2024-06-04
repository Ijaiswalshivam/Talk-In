package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.talk_in.databinding.ActivityGroupsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Groups : AppCompatActivity() {
    private lateinit var binding: ActivityGroupsBinding
    private lateinit var adapter: DisplayGroupAdapter
    private lateinit var groupList: ArrayList<Group>
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDbRef = FirebaseDatabase.getInstance().getReference()

        groupList = ArrayList()
        adapter = DisplayGroupAdapter(this, groupList)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = adapter

        mDbRef.child("groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentGroup = postSnapshot.getValue(Group::class.java)
                    groupList.add(currentGroup!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })

        binding.addGroupBtn.setOnClickListener {
            val intent = Intent(this@Groups, GroupMemberSelection::class.java)
            startActivity(intent)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                    val intent = Intent(this@Groups, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    true
                }
                R.id.menu_group -> true
                R.id.menu_profile -> {
                    val intent = Intent(this, UserProfileScreen::class.java)
                    intent.putExtra("MODE", "CURRENT_USER")
                    finish()
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}