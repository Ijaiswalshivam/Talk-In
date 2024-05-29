import 'dart:convert';
import 'dart:html';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_storage/firebase_storage.dart';
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
        "password":password,
        "about":"Hey! I am using TalkIn",
        "profileVisibility":"true"
      });
      UserService.userData = {
        "id":authId,
        "name":name,
        "email":email,
        "password":password,
        "about":"Hey! I am using TalkIn",
        "profileVisibility":"true",
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
      //print(data);
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
        //print(data);
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

  Future<void> unFriendUser(BuildContext context,String friendId) async{
    try{
      setLoading(true);
      String currentUserAuthId = UserService.userData!["id"].toString();
      await userRef.child(currentUserAuthId).child("Friends").child(friendId).remove();
      await userRef.child(friendId).child("Friends").child(currentUserAuthId).remove();
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
        //print(data);
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
        // print(data);
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

  void addChatToDatabase(BuildContext context,String messageId, String text, String senderId, String receiverId,DateTime dateTime,String tag) async{
    try{
      print("11111111111111111111111111111111111111111111111111");
      if(tag=="text"){
        await messageRef.child(messageId).push().set({
          "messageId":messageId,
          "SendBy":senderId,
          "ReceivedBy":receiverId,
          "Message":text,
          "MediaUrl":"",
          "DateTime":dateTime.toString(),
          "tag":tag
        });
      }else{
        print("11111111111111111111111111111111111111111111111111");
        String fileAccept = "";
        if(tag=="image"){
          fileAccept = "image/*";
        }else if(tag=="document"){
          fileAccept = "application/pdf";
        }else{
          fileAccept = "audio/mp3";
        }
        print("11111111111111111111111111111111111111111111111111");
        FileUploadInputElement fileUploadInputElement = FileUploadInputElement();
        fileUploadInputElement.multiple = false;
        fileUploadInputElement.accept = fileAccept;
        fileUploadInputElement.click();
        fileUploadInputElement.onChange.listen((e) {
          final files = fileUploadInputElement.files;
          if(files!.length==1){
            final file = files[0];
            final reader = new FileReader();
            print("123");
            reader.onLoadEnd.listen((e) async {
              print("11111111111111111111111111111111111111111111111111");
              print(e);
              print('loaded: ${file.name}');
              print('type: ${reader.result.runtimeType}');
              print('file size = ${file.size}');
              if((file.size/1024)/1024<=10){
                final encoded = reader.result.toString();
                final base64d = base64.decode(encoded.split(',').last);
                print(base64d);
                await FirebaseStorage.instance.ref("MessageMedia")
                    .child(messageId)
                    .child(dateTime.toString())
                    .putData(base64d,/**SettableMetadata(contentType: fileAccept)**/)
                    .then((taskSnapshot) async{
                  String url = await taskSnapshot.ref.getDownloadURL();
                  print("11111111111111111111111111111111111111111111111111");
                  await messageRef.child(messageId).push().set({
                    "messageId":messageId,
                    "SendBy":senderId,
                    "ReceivedBy":receiverId,
                    "Message":text,
                    "MediaUrl":url,
                    "DateTime":dateTime.toString(),
                    "tag":tag
                  });
                  print("Media Sent Successfully");
                });
              }else{
                print("LARGE FILE");
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("File Size exceeds 10 MB limit",style: TextStyle(color: Colors.white),)));
              }
            });
            reader.readAsDataUrl(file);
          }else{
            print("NO FILE CHOSEN");
          }
        });

      }
    }catch(e){
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
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
        //print(data);
        final result = jsonDecode(jsonEncode(data));
        requestsList.add(result);
      }
      loadSentRequests(requestsList);
    }catch(e){
      print(e);
      loadSentRequests([]);
    }
  }

  void addGroupChatToDatabase(BuildContext context,String text,String senderId,String tag,DateTime dateTime,String groupId) async{
    final groupRef = FirebaseDatabase.instance.ref("Groups").child(groupId).child("Messages");
    try{
      if(tag=="text"){
        await groupRef.push().set({
          "Message":text,
          "SendBy":senderId,
          "tag":tag,
          "DateTime":dateTime.toString(),
          "MediaUrl":""
        });
      }else{
        print("11111111111111111111111111111111111111111111111111");
        String fileAccept = "";
        if(tag=="image"){
          fileAccept = "image/*";
        }else if(tag=="document"){
          fileAccept = "application/pdf";
        }else{
          fileAccept = "audio/mp3";
        }
        print("11111111111111111111111111111111111111111111111111");
        FileUploadInputElement fileUploadInputElement = FileUploadInputElement();
        fileUploadInputElement.multiple = false;
        fileUploadInputElement.accept = fileAccept;
        fileUploadInputElement.click();
        fileUploadInputElement.onChange.listen((e) {
          final files = fileUploadInputElement.files;
          if(files!.length==1){
            final file = files[0];
            final reader = new FileReader();
            print("123");
            reader.onLoadEnd.listen((e) async {
              print("11111111111111111111111111111111111111111111111111");
              print(e);
              print('loaded: ${file.name}');
              print('type: ${reader.result.runtimeType}');
              print('file size = ${file.size}');
              if((file.size/1024)/1024<=10){
                final encoded = reader.result.toString();
                final base64d = base64.decode(encoded.split(',').last);
                print(base64d);
                await FirebaseStorage.instance.ref("MessageMedia")
                    .child(groupId)
                    .child(dateTime.toString())
                    .putData(base64d,/**SettableMetadata(contentType: fileAccept)**/)
                    .then((taskSnapshot) async{
                  String url = await taskSnapshot.ref.getDownloadURL();
                  print("11111111111111111111111111111111111111111111111111");
                  await groupRef.push().set({
                    "Message":text,
                    "SendBy":senderId,
                    "tag":tag,
                    "DateTime":dateTime.toString(),
                    "MediaUrl":url
                  });
                  print("Media Sent Successfully");
                });
              }else{
                print("LARGE FILE");
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("File Size exceeds 10 MB limit",style: TextStyle(color: Colors.white),)));
              }
            });
            reader.readAsDataUrl(file);
          }else{
            print("NO FILE CHOSEN");
          }
        });

      }
    }catch(e){
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      print(e);
    }
  }

}