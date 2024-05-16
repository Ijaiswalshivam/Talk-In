import 'dart:async';
import 'dart:convert';

import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter_chat_bubble/chat_bubble.dart';
import 'package:talk_in_web/services/user_service.dart';

import '../../services/data_service.dart';
class MessagingScreen extends StatefulWidget {
  late Map<String,dynamic> friendData;
  MessagingScreen(this.friendData);

  @override
  State<MessagingScreen> createState() => _MessagingScreenState(friendData);
}

class _MessagingScreenState extends State<MessagingScreen> {
  late Map<String,dynamic> friendData;
  late String messageId;
  _MessagingScreenState(this.friendData){
    String sender = UserService.userData!["email"].toString().replaceAll(".", "").replaceAll("\$", "").replaceAll("-", "").replaceAll("+", "").replaceAll("_", "");
    String receiver = friendData["email"].toString().replaceAll(".", "").replaceAll("\$", "").replaceAll("-", "").replaceAll("+", "").replaceAll("_", "");
    messageId = sender.compareTo(receiver)>0?receiver+sender:sender+receiver;
  }

  TextEditingController messageController = TextEditingController();
  ScrollController scrollController=ScrollController();

  DatabaseReference messageRef = FirebaseDatabase.instance.ref("Messages");

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Row(
          children: [
            friendData["profilePic"].toString()=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(friendData["profilePic"].toString(),width: 30,height:30,),
            Padding(
              padding: const EdgeInsets.all(12.0),
              child: Text(friendData["name"].toString(),style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
            ),
          ],
        ),
      ),
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: ConstrainedBox(
          constraints: BoxConstraints(
            minWidth: MediaQuery.of(context).size.width,
            minHeight: MediaQuery.of(context).size.height-100,
          ),
          child: IntrinsicHeight(
            child: Column(
              children: [

                Container(
                  height: MediaQuery.of(context).size.height-200,
                  width: MediaQuery.of(context).size.width,
                  child: FirebaseAnimatedList(
                      controller: scrollController,
                      shrinkWrap: true,
                      query: messageRef.child(messageId),
                      itemBuilder: (BuildContext context,DataSnapshot snapshot,Animation<double> animation,int index){
                        final result=snapshot.value;
                        final data = jsonDecode(jsonEncode(result));
                        print(data);
                        bool isUserMessage=data["SendBy"].toString()==UserService.userData!["id"].toString();
                        Timer(Duration(milliseconds: 500), () {
                          scrollController.jumpTo(scrollController.position.maxScrollExtent);
                        });
                        return isUserMessage?
                        ChatBubble(
                          clipper: ChatBubbleClipper1(type: BubbleType.sendBubble),
                          alignment: Alignment.topRight,
                          margin: EdgeInsets.only(top: 20),
                          backGroundColor: Colors.blue,
                          child: Container(
                            constraints: BoxConstraints(
                              maxWidth: MediaQuery.of(context).size.width * 0.7,
                            ),
                            child: Text(
                              data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                              style: TextStyle(color: Colors.white),
                            ),
                          ),
                        )
                            :
                        ChatBubble(
                          clipper: ChatBubbleClipper1(type: BubbleType.receiverBubble),
                          backGroundColor: Color(0xffE7E7ED),
                          margin: EdgeInsets.only(top: 20),
                          child: Container(
                            constraints: BoxConstraints(
                              maxWidth: MediaQuery.of(context).size.width * 0.7,
                            ),
                            child: Text(
                              data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                              style: TextStyle(color: Colors.black),
                            ),
                          ),
                        );
                      }
                  ),
                ),

                SizedBox(height: 10,),

                Expanded(
                  child: Align(
                    alignment: FractionalOffset.bottomCenter,
                    child: Container(
                      height: 69,
                      width: MediaQuery.of(context).size.width,
                      margin: EdgeInsets.all(5),
                      padding: EdgeInsets.symmetric(horizontal: 20/2, vertical: 20 / 3),
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.all(Radius.circular(15)),
                          color: Theme.of(context).scaffoldBackgroundColor,
                          boxShadow: [BoxShadow(blurRadius: 30, offset: Offset(0, 4), color: Color(0xff087949).withOpacity(0.7))]),
                      child: SafeArea(
                          child: Row(
                            children: [
                              Expanded(
                                child: Container(
                                  padding: EdgeInsets.symmetric(horizontal: 20 * 0.75),
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(20),
                                  ),
                                  child: Row(
                                    children: [
                                      Expanded(
                                        child: TextField(
                                          controller: messageController,
                                          keyboardType: TextInputType.multiline,
                                          maxLines: null,
                                          decoration: InputDecoration(border: InputBorder.none, hintText: 'Type Message'),
                                        ),
                                      ),
                                      SizedBox(width: 20 / 4),
                                      FloatingActionButton(
                                        backgroundColor: Colors.lightGreen,
                                        onPressed: () async{

                                          if(messageController.text.isNotEmpty){
                                            DataService().addChatToDatabase(messageId, messageController.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now());
                                            messageController.clear();
                                            Timer(Duration(milliseconds: 500), () {
                                              scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                            });
                                          }
                                        },
                                        child: Icon(Icons.send,color: Colors.white,),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          )
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        )
      ),
    );
  }
}
