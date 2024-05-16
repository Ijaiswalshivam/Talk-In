import 'dart:convert';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/services/user_service.dart';

class DataService{


  DatabaseReference userRef = FirebaseDatabase.instance.ref("Users");
  DatabaseReference messageRef = FirebaseDatabase.instance.ref("Messages");

  Future<bool> addUserToFirebase(BuildContext context,String authId,String name,String email,String password) async{
    try{
      await userRef.child(authId).set({
        "id":authId,
        "name":name,
        "email":email,
        "password":password
      });
      UserService.userData = {
        "id":authId,
        "name":name,
        "email":email,
        "password":password
      };
      return true;
    }catch(e){
      print(e);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      return false;
    }
  }

  Future<void> getUserFromFirebase(BuildContext context,String authId) async{
    try{
      DataSnapshot snapshot = await userRef.child(authId).get();
      final data = snapshot.value;
      print(data);
      final result = jsonDecode(jsonEncode(data));
      UserService.userData = result;
      print(UserService.userData);
    }catch(e){
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

  Future<List<Map<String, dynamic>>> getAllUsersExceptCurrentUser() async{
    try{
      DataSnapshot snapshot = await userRef.get();
      List<Map<String,dynamic>> userList = [];
      for(DataSnapshot snap in snapshot.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        if(result["id"].toString()!=UserService.userData!["id"].toString())
          userList.add(result);
      }
      return userList;
    }catch(e){
      print(e);
      return [];
    }
  }

  Future<void> sendFriendRequest(BuildContext context,Map<String,dynamic> user) async{
    try{
      String currentUserAuthId = UserService.userData!["id"].toString();
      final requestRef = userRef.child(currentUserAuthId).child("Requests");
      await requestRef.child(user["id"]).set(user);
    }catch(e){
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

  Future<void> acceptFriendRequest(BuildContext context,Map<String,dynamic> user) async{
    try{
      String currentUserAuthId = UserService.userData!["id"].toString();
      final friendsRef = userRef.child(currentUserAuthId).child("Friends");
      await friendsRef.child(user["id"].toString()).set(user);
      await userRef.child(currentUserAuthId).child("Requests").child(user["id"]).remove();
      final friendsRef1 = userRef.child(user["id"].toString()).child("Friends");
      await friendsRef1.child(currentUserAuthId).set(UserService.userData);
    }catch(e){
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

  Future<List<Map<String, dynamic>>> getRequestsList() async{
    try{
      DataSnapshot snapshot = await userRef.child(UserService.userData!["id"]).get();
      final requestSnap = snapshot.child("Requests");
      List<Map<String,dynamic>> requestsList = [];
      for(DataSnapshot snap in requestSnap.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        requestsList.add(result);
      }
      return requestsList;
    }catch(e){
      print(e);
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> getFriendsList() async{
    try{
      DataSnapshot snapshot = await userRef.child(UserService.userData!["id"]).get();
      final friendSnap = snapshot.child("Friends");
      List<Map<String,dynamic>> friendsList = [];
      for(DataSnapshot snap in friendSnap.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        friendsList.add(result);
      }
      return friendsList;
    }catch(e){
      print(e);
      return [];
    }
  }

  void addChatToDatabase(String messageId, String text, String senderId, String receiverId,DateTime dateTime) async{
    try{
      await messageRef.child(messageId).push().set({
        "messageId":messageId,
        "SendBy":senderId,
        "ReceivedBy":receiverId,
        "Message":text,
        "DateTime":dateTime.toString()
      });
    }catch(e){
      print(e);
    }
  }

}