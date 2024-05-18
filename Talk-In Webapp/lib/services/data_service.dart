import 'dart:convert';
import 'dart:js_util';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/services/user_service.dart';

class DataService extends ChangeNotifier{

  bool _loading = false;
  bool get loading => _loading;

  List<Map<String,dynamic>> _friendList = [];
  List<Map<String,dynamic>> get friendList => _friendList;

  List<Map<String,dynamic>> _requestList = [];
  List<Map<String,dynamic>> get requestList => _requestList;

  List<Map<String,dynamic>> _sentRequestList = [];
  List<Map<String,dynamic>> get sentRequestList => _sentRequestList;

  List<Map<String,dynamic>> _userList = [];
  List<Map<String,dynamic>> get userList => _userList;

  void setLoading(bool value){
    _loading = value;
    notifyListeners();
  }

  void loadFriends(List<Map<String,dynamic>> list){
    _friendList = list;
    notifyListeners();
  }

  void loadRequests(List<Map<String,dynamic>> list){
    _requestList = list;
    notifyListeners();
  }

  void loadSentRequests(List<Map<String,dynamic>> list){
    _sentRequestList = list;
    notifyListeners();
  }

  void loadUsers(List<Map<String,dynamic>> list){
    _userList = list;
    notifyListeners();
  }


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

  Future<void> getAllUsersExceptCurrentUser() async{
    try{
      setLoading(true);
      DataSnapshot snapshot = await userRef.get();
      List<Map<String,dynamic>> usersList = [];
      for(DataSnapshot snap in snapshot.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        if(result["id"].toString()!=UserService.userData!["id"].toString())
          usersList.add(result);
      }
      setLoading(false);
      loadUsers(usersList);
      await getFriendsList();
      await getMySentRequests();
    }catch(e){
      print(e);
      setLoading(false);
      loadUsers([]);
    }
  }

  Future<void> sendFriendRequest(BuildContext context,Map<String,dynamic> user) async{
    try{
      setLoading(true);
      String currentUserAuthId = UserService.userData!["id"].toString();
      DatabaseReference requestRef = userRef.child(user["id"]).child("ReceivedRequests");
      await requestRef.child(currentUserAuthId).set(UserService.userData);
      requestRef = userRef.child(currentUserAuthId).child("SentRequests");
      await requestRef.child(user["id"]).set(user);
      setLoading(false);
    }catch(e){
      setLoading(false);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

  Future<void> acceptFriendRequest(BuildContext context,Map<String,dynamic> user) async{
    try{
      setLoading(true);
      String currentUserAuthId = UserService.userData!["id"].toString();
      final friendsRef = userRef.child(currentUserAuthId).child("Friends");
      await friendsRef.child(user["id"].toString()).set(user);
      final friendsRef1 = userRef.child(user["id"].toString()).child("Friends");
      await friendsRef1.child(currentUserAuthId).set(UserService.userData);
      await userRef.child(currentUserAuthId).child("ReceivedRequests").child(user["id"]).remove();
      await userRef.child(user["id"]).child("SentRequests").child(currentUserAuthId).remove();
      setLoading(false);
    }catch(e){
      setLoading(false);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

  void getRequestsList() async{
    try{
      setLoading(true);
      DataSnapshot snapshot = await userRef.child(UserService.userData!["id"]).get();
      final requestSnap = snapshot.child("ReceivedRequests");
      List<Map<String,dynamic>> requestsList = [];
      for(DataSnapshot snap in requestSnap.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        requestsList.add(result);
      }
      setLoading(false);
      loadRequests(requestsList);
    }catch(e){
      print(e);
      setLoading(false);
      loadRequests([]);
    }
  }

  Future<void> getFriendsList() async{
    try{
      setLoading(true);
      DataSnapshot snapshot = await userRef.child(UserService.userData!["id"]).get();
      final friendSnap = snapshot.child("Friends");
      List<Map<String,dynamic>> friendsList = [];
      for(DataSnapshot snap in friendSnap.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        friendsList.add(result);
      }
      setLoading(false);
      loadFriends(friendsList);
    }catch(e){
      print(e);
      setLoading(false);
      loadFriends([]);
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

  Future<void> getMySentRequests() async{
    try{
      DataSnapshot snapshot = await userRef.child(UserService.userData!["id"]).get();
      final requestSnap = snapshot.child("SentRequests");
      List<Map<String,dynamic>> requestsList = [];
      for(DataSnapshot snap in requestSnap.children){
        final data = snap.value;
        print(data);
        final result = jsonDecode(jsonEncode(data));
        requestsList.add(result);
      }
      loadSentRequests(requestsList);
    }catch(e){
      print(e);
      loadSentRequests([]);
    }
  }

}