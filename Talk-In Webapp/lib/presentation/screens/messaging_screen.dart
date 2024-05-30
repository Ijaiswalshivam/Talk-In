import 'dart:async';
import 'dart:convert';
import 'package:talk_in_web/generated/l10n.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter_chat_bubble/chat_bubble.dart';
import 'package:talk_in_web/presentation/screens/profile_screen.dart';
import 'package:talk_in_web/services/user_service.dart';
import 'package:url_launcher/url_launcher.dart';

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
            friendData["profilePic"].toString()=="null"?Image.asset("assets/images/profile.png",width: 40,height:40,):Image.network(friendData["profilePic"].toString(),width: 40,height:40,),
            Padding(
              padding: const EdgeInsets.all(12.0),
              child: Text(friendData["name"].toString(),style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
            ),
          ],
        ),
        actions: [
          TextButton.icon(
              onPressed: (){
                Navigator.push(context, MaterialPageRoute(builder: (context){
                  return ProfileScreen(friendData);
                }));
              },
              icon: Icon(Icons.person,color: Colors.white,),
              label: Text("${friendData["name"].toString()}${S.of(context).Profile}",style: TextStyle(fontWeight: FontWeight.bold,fontSize: 15,color: Colors.white),)
          ),
        ],
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
                          //print(data);
                          bool isUserMessage=data["SendBy"].toString()==UserService.userData!["id"].toString();
                          Timer(Duration(milliseconds: 300), () {
                            scrollController.jumpTo(scrollController.position.maxScrollExtent);
                          });
                          return isUserMessage?
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Padding(
                                padding: const EdgeInsets.all(14.0),
                                child: Text(" Send on ${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year} at ${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute} ",style: TextStyle(color: Colors.white),),
                              ),
                              ChatBubble(
                                clipper: ChatBubbleClipper1(type: BubbleType.sendBubble),
                                alignment: Alignment.topRight,
                                margin: EdgeInsets.only(top: 20),
                                backGroundColor: Colors.blue,
                                child: Container(
                                  constraints: BoxConstraints(
                                    maxWidth: MediaQuery.of(context).size.width/4,
                                  ),
                                  child: data["tag"]=="text"? Column(
                                    children: [
                                      Text(
                                        data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                                        style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),
                                      ),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
                                    ],
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          )
                              :
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              ChatBubble(
                                clipper: ChatBubbleClipper1(type: BubbleType.receiverBubble),
                                backGroundColor: Color(0xffE7E7ED),
                                margin: EdgeInsets.only(top: 20),
                                child: Container(
                                  constraints: BoxConstraints(
                                    maxWidth: MediaQuery.of(context).size.width * 0.7,
                                  ),
                                  child: data["tag"]=="text"? Column(
                                    children: [
                                      Text(
                                        data["Message"]!=null?data["Message"].toString():"Waiting for the message...",
                                        style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold),
                                      ),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
                                    ],
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
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
                                      Text(data["Message"]!=null?data["Message"].toString():"Waiting for the message...", style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold)),
                                      // Row(
                                      //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      //   children: [
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year}",style: TextStyle(color: Colors.white),),
                                      //     Text("${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute}",style: TextStyle(color: Colors.white),),
                                      //   ],
                                      // ),
                                    ],
                                  ),
                                ),
                              ),
                              Padding(
                                padding: const EdgeInsets.all(14.0),
                                child: Text(" Received on ${DateTime.parse(data["DateTime"].toString()).day}-${DateTime.parse(data["DateTime"].toString()).month}-${DateTime.parse(data["DateTime"].toString()).year} at ${DateTime.parse(data["DateTime"].toString()).hour}:${DateTime.parse(data["DateTime"].toString()).minute} ",style: TextStyle(color: Colors.white),),
                              ),
                            ],
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
                                              hintText: S.of(context).TypeMessage1,
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
                                                                  hintText: S.of(context).TypeMessage2,
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"image");
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text(S.of(context).No)),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"image");
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text(S.of(context).Yes)),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.image,color: Colors.black,),
                                                        label:Text(S.of(context).Images,style: TextStyle(color: Colors.black),)
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
                                                                  hintText: S.of(context).TypeMessage2,
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"document");
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text(S.of(context).No)),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"document");
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text(S.of(context).Yes)),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.picture_as_pdf,color: Colors.black,),
                                                        label:Text(S.of(context).Document,style: TextStyle(color: Colors.black),)
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
                                                                  hintText: S.of(context).TypeMessage2,
                                                                ),
                                                              ),
                                                              actions: [
                                                                TextButton(onPressed: (){
                                                                  Navigator.of(c).pop();
                                                                  DataService().addChatToDatabase(context, messageId, "", UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"audio");
                                                                  Timer(Duration(milliseconds: 500), () {
                                                                    scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                  });
                                                                }, child: Text(S.of(context).No)),
                                                                TextButton(onPressed: (){
                                                                  if(controller.text.isNotEmpty){
                                                                    Navigator.of(c).pop();
                                                                    DataService().addChatToDatabase(context, messageId, controller.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"audio");
                                                                    Timer(Duration(milliseconds: 500), () {
                                                                      scrollController.jumpTo(scrollController.position.maxScrollExtent);
                                                                    });
                                                                  }
                                                                }, child: Text(S.of(context).Yes)),
                                                              ],
                                                            );
                                                          });
                                                        },
                                                        icon: Icon(Icons.audio_file,color: Colors.black,),
                                                        label:Text(S.of(context).Audio,style: TextStyle(color: Colors.black),)
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
                                              DataService().addChatToDatabase(context,messageId, messageController.text, UserService.userData!["id"].toString(), friendData["id"].toString(),DateTime.now(),"text");
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
