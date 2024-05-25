import 'dart:async';
import 'dart:convert';

import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter_chat_bubble/chat_bubble.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../services/data_service.dart';
import '../../services/user_service.dart';
class GroupChatScreen extends StatefulWidget {
  late Map<String,dynamic> groupData;
  GroupChatScreen(this.groupData);

  @override
  State<GroupChatScreen> createState() => _GroupChatScreenState(groupData);
}

class _GroupChatScreenState extends State<GroupChatScreen> {

  late Map<String,dynamic> groupData;
  _GroupChatScreenState(this.groupData);

  TextEditingController messageController = TextEditingController();
  ScrollController scrollController = ScrollController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text(groupData["name"].toString(),style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
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
                        query: FirebaseDatabase.instance.ref("Groups").child(groupData["id"].toString()).child("Messages"),
                        itemBuilder: (BuildContext context,DataSnapshot snapshot,Animation<double> animation,int index){
                          final result=snapshot.value;
                          final data = jsonDecode(jsonEncode(result));
                          //print(data);
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
                              child: data["tag"]=="text"? Text(
                                data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                                style: TextStyle(color: Colors.white),
                              ): data["tag"] == "image"? Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.image,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
                              ) : data["tag"] == "document"? Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.picture_as_pdf,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
                              ) : Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.audio_file,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
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
                              child: data["tag"]=="text"? Text(
                                data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                                style: TextStyle(color: Colors.white),
                              ): data["tag"] == "image"? Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.image,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
                              ) : data["tag"] == "document"? Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.picture_as_pdf,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
                              ) : Column(
                                children: [
                                  Stack(
                                    children: [
                                      Icon(Icons.audio_file,color: Colors.black,size: 100,),
                                      IconButton(onPressed: () async{
                                        await launchUrl(Uri.parse(data["MediaUrl"].toString()));
                                      },
                                          icon: Icon(Icons.download,color: Colors.white,size: 40,)
                                      )
                                    ],
                                  ),
                                  Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white)),
                                ],
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
                                            decoration: InputDecoration(
                                              border: InputBorder.none,
                                              hintText: 'Type Message',
                                              prefixIcon: PopupMenuButton<int>(
                                                icon: Icon(Icons.attach_file,color: Colors.black,),
                                                iconSize: 20,
                                                iconColor: Colors.black,
                                                shadowColor: Colors.red,
                                                itemBuilder: (context)=>[
                                                  PopupMenuItem(
                                                    value: 1,
                                                    child: TextButton.icon(
                                                        onPressed: (){
                                                          //tag=image
                                                          showDialog(context: context, builder:(BuildContext c){
                                                            TextEditingController controller = TextEditingController();
                                                            return AlertDialog(
                                                              title: TextField(
                                                                controller: controller,
                                                                keyboardType: TextInputType.multiline,
                                                                maxLines: null,
                                                                decoration: InputDecoration(
                                                                  border: InputBorder.none,
                                                                  hintText: 'Wanna Type A Message',
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  //DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"image");
                                                                  DataService().addGroupChatToDatabase(context, "", UserService.userData!["id"].toString(), "image", DateTime.now(), groupData["id"].toString());
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text("NO")),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    //DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"image");
                                                                    DataService().addGroupChatToDatabase(context, controller.text, UserService.userData!["id"].toString(), "image", DateTime.now(), groupData["id"].toString());
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text("YES")),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.image,color: Colors.black,),
                                                        label:Text("Images",style: TextStyle(color: Colors.black),)
                                                    ),
                                                  ),
                                                  PopupMenuItem(
                                                    value: 2,
                                                    child: TextButton.icon(
                                                        onPressed: (){
                                                          //tag=document
                                                          showDialog(context: context, builder:(BuildContext c){
                                                            TextEditingController controller = TextEditingController();
                                                            return AlertDialog(
                                                              title: TextField(
                                                                controller: controller,
                                                                keyboardType: TextInputType.multiline,
                                                                maxLines: null,
                                                                decoration: InputDecoration(
                                                                  border: InputBorder.none,
                                                                  hintText: 'Wanna Type A Message',
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  //DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"document");
                                                                  DataService().addGroupChatToDatabase(context, "", UserService.userData!["id"].toString(), "document", DateTime.now(), groupData["id"].toString());
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text("NO")),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    //DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"document");
                                                                    DataService().addGroupChatToDatabase(context, controller.text, UserService.userData!["id"].toString(), "document", DateTime.now(), groupData["id"].toString());
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text("YES")),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.picture_as_pdf,color: Colors.black,),
                                                        label:Text("Document",style: TextStyle(color: Colors.black),)
                                                    ),
                                                  ),
                                                  PopupMenuItem(
                                                    value: 3,
                                                    child: TextButton.icon(
                                                        onPressed: (){
                                                          //tag=audio
                                                          showDialog(context: context, builder:(BuildContext c){
                                                            TextEditingController controller = TextEditingController();
                                                            return AlertDialog(
                                                              title: TextField(
                                                                controller: controller,
                                                                keyboardType: TextInputType.multiline,
                                                                maxLines: null,
                                                                decoration: InputDecoration(
                                                                  border: InputBorder.none,
                                                                  hintText: 'Wanna Type A Message',
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  //DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"audio");
                                                                  DataService().addGroupChatToDatabase(context, "", UserService.userData!["id"].toString(), "audio", DateTime.now(), groupData["id"].toString());
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text("NO")),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    //DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"audio");
                                                                    DataService().addGroupChatToDatabase(context, controller.text, UserService.userData!["id"].toString(), "audio", DateTime.now(), groupData["id"].toString());
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text("YES")),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.audio_file,color: Colors.black,),
                                                        label:Text("Audio",style: TextStyle(color: Colors.black),)
                                                    ),
                                                  ),
                                                ],
                                                offset: Offset(0, 100),
                                                color: Colors.white,
                                                elevation: 3,
                                                onSelected: (value) {
                                                  if (value == 1) {

                                                  } else if (value == 2) {

                                                  }else{

                                                  }
                                                },
                                              ),
                                            ),
                                          ),
                                        ),
                                        SizedBox(width: 20 / 4),
                                        FloatingActionButton(
                                          backgroundColor: Colors.lightGreen,
                                          onPressed: () async{

                                            if(messageController.text.isNotEmpty){
                                              //DataService().addChatToDatabase(context,messageId, messageController.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"text");
                                              DataService().addGroupChatToDatabase(context, messageController.text, UserService.userData!["id"].toString(), "text", DateTime.now(), groupData["id"].toString());
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
