package com.example.talk_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GroupChatActivity : AppCompatActivity() {
    private lateinit var groupChatRecyclerView: RecyclerView
    private lateinit var nameOfGroup: TextView
    private lateinit var backbtnImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        Log.d("GroupChatActivity", "onCreate() method called")

        val name = intent.getStringExtra("NAME")
        val groupId = intent.getStringExtra("GROUP_ID")
        supportActionBar?.hide()

        groupChatRecyclerView = findViewById(R.id.chatRecyclerView)
        nameOfGroup = findViewById(R.id.nameOfUser)
        backbtnImage = findViewById(R.id.backbtnImage)

        nameOfGroup.text = name

        backbtnImage.setOnClickListener {
            val intent = Intent(this@GroupChatActivity, Groups::class.java)
            finish()
            startActivity(intent)
        }

        val popupMenuBtn: ImageView = findViewById(R.id.popupMenuBtn)
        popupMenuBtn.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.user_chat_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.viewProfile -> {
                    Toast.makeText(this, "View Profile Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.sharedMedia -> {
                    Toast.makeText(this, "Shared Media Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.locateNow -> {
                    Toast.makeText(this, "Locate Now Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}