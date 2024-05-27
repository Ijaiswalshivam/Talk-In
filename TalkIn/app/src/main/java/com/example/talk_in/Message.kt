package com.example.talk_in

data class Message(
        val message: String? = null,
        val senderId: String? = null,
        val timestamp: Long? = null,
        val isDateSection: Boolean = false
)
