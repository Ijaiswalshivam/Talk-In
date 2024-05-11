package com.example.talk_in

class Message {
    var message: String?= null
    var senderId: String?=null
    constructor(){}
    constructor(message: String?,senderId: String?){
        this.message=message
        this.senderId=senderId
    }
    public fun sendMessage(){
        //send message
        Toast.makeText( this@Message, "Message sent at : " + Instant.ofEpochMilli((System.currentTimeMillis()/1000)), Toast.LENGTH_SHORT).show()
    }
}