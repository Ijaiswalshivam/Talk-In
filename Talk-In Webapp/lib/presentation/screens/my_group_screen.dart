import 'dart:convert';

import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/group_chat_screen.dart';
import 'package:talk_in_web/services/user_service.dart';
class MyGroupsScreen extends StatefulWidget {
  const MyGroupsScreen({super.key});

  @override
  State<MyGroupsScreen> createState() => _MyGroupsScreenState();
}

class _MyGroupsScreenState extends State<MyGroupsScreen> {

  ScrollController scrollController = ScrollController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text("My Groups",style: TextStyle(color: Colors.white),),
      ),
      body: FirebaseAnimatedList(
          shrinkWrap: true,
          controller: scrollController,
          query: FirebaseDatabase.instance.ref("Groups"),
          itemBuilder: (BuildContext context,DataSnapshot snapshot,Animation<double> animation,int index){
            final result = snapshot.value;
            final data = jsonDecode(jsonEncode(result));
            print(data);
            List<String> members = (data["Members"] as List<dynamic>).map((e) => e.toString()).toList();
            if(members.contains(UserService.userData!["id"])){
              String groupId = data["id"].toString();
              return ListTile(
                onTap: (){
                  Navigator.of(context).push(MaterialPageRoute(builder: (context){
                    return GroupChatScreen(data);
                  }));
                },
                title: Text(data["name"].toString(),style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              );
            }
            return Container();
          }
      ),
    );
  }
}
