package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter=UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
<<<<<<< HEAD
            // Remove device token from Firebase database
            val currentUser = mAuth.currentUser
            currentUser?.uid?.let { userId ->
                mDbRef.child("users-device-tokens").child(userId).removeValue()
                    .addOnSuccessListener {
                        mAuth.signOut()
                        val intent = Intent(this@MainActivity, LogIn::class.java)
                        finish()
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                    }
            }
=======
            //logic for logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity,EntryActivity::class.java)
            startActivity(intent)
            finish()
           // finish()
>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f
            return true
        }
        return super.onOptionsItemSelected(item)
    }
<<<<<<< HEAD

=======
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
>>>>>>> 9ad769bdd105a202435e8abca418a2e893826d8f
}