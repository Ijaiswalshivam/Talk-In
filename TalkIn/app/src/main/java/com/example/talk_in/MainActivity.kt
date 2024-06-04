package com.example.talk_in

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.talk_in.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var tempUserList: ArrayList<User>
    private lateinit var tempAdapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        tempUserList = ArrayList()
        tempAdapter = UserAdapter(this, tempUserList)

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

        binding.userSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No needed to implement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No needed to implement
            }

            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }
        })

        binding.searchIcon.setOnClickListener {
            binding.userSearchBar.visibility = View.VISIBLE
            binding.userSearchBar.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.userSearchBar, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> true
                R.id.menu_group -> {
                    val intent = Intent(this, Groups::class.java)
                    finish()
                    startActivity(intent)
                    true
                }
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

    private fun filterList(text: String) {
        tempUserList.clear()
        binding.userRecyclerView.adapter = tempAdapter
        for (user in userList) {
            if (user.name?.startsWith(text, ignoreCase = true) == true) {
                tempUserList.add(user)
            }
        }
        if (tempUserList.isEmpty()) {
            tempAdapter.notifyDataSetChanged()
            Toast.makeText(this@MainActivity, "No data found", Toast.LENGTH_SHORT).show()
        } else {
            tempAdapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        if (binding.userSearchBar.visibility == View.VISIBLE) {
            binding.userSearchBar.setText("")
            binding.userSearchBar.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}