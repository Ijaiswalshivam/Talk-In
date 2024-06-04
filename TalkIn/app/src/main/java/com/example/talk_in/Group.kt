package com.example.talk_in

class Group {
    var groupName: String? = null
    var groupDescription: String? = null
    var groupId: String? = null
    var groupMembers: ArrayList<String>? = null

    constructor(){}

    constructor(groupName: String?, groupDescription: String?, groupMembers: ArrayList<String>?, groupId: String?){
        this.groupName = groupName
        this.groupDescription = groupDescription
        this.groupMembers = groupMembers
        this.groupId = groupId
    }
}