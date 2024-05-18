package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.search_username_input)
        searchButton = findViewById(R.id.search_button2)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.search_user_recycler_view)

        searchInput.requestFocus()

        val onClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Perform an action when the button is clicked
            }
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        searchButton.setOnClickListener {
            val searchTerm = searchInput.text.toString()
            if (searchTerm.isEmpty() || searchTerm.length < 3) {
                searchInput.setError("Invalid Username")
                return@setOnClickListener
            }
            // Call your method to setup the recycler view here
            setupSearchRecyclerView(searchTerm)
        }
    }

    private fun setupSearchRecyclerView(searchTerm: String) {
        // Replace this with your logic to setup the recycler view
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}
