package com.example.talk_in

class User {
    var name: String? = null
    var email: String? = null
    var mobile: String? = null
    var uid: String? = null

    constructor(){}

    constructor(name: String?, email: String?, mobile: String?, uid: String?){
        this.name = name
        this.email = email
        this.mobile = mobile
        this.uid = uid
    }
}