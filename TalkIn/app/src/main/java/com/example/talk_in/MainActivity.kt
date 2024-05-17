package com.example.talk_in

import CameraFragment
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var chatFragment: ChatListFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var groupFragment: GroupFragment
    private lateinit var cameraFragment: CameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatFragment = ChatListFragment()
        profileFragment = ProfileFragment()
        groupFragment = GroupFragment()
        cameraFragment = CameraFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                    replaceFragment(chatFragment)
                    true
                }
                R.id.menu_groups -> {
                    replaceFragment(groupFragment)
                    true
                }
                R.id.menu_profile -> {
                    replaceFragment(profileFragment)
                    true
                }
                R.id.menu_send -> {
                    replaceFragment(cameraFragment)
                    true
                }
                R.id.menu_search ->{
                    val intent =Intent(this,SearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.menu_chat
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}