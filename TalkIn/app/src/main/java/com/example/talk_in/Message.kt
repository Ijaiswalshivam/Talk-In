package com.example.talk_in

data class Message(
        var message: String? = null,
        val senderId: String? = null,
        val timestamp: Long? = null,
        val isDateSection: Boolean = false
)