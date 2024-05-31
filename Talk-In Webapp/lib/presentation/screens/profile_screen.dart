import 'dart:convert';
import 'package:talk_in_web/generated/l10n.dart';
import 'package:expandable_menu/expandable_menu.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/services/user_service.dart';
class ProfileScreen extends StatefulWidget {
  late Map<String,dynamic> friendData;
  ProfileScreen(this.friendData);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState(this.friendData);
}

class _ProfileScreenState extends State<ProfileScreen> {
  late Map<String,dynamic> friendData;
  _ProfileScreenState(this.friendData);

  List<Widget> mutualFriends = [];

  void getMutualFriends() async{
    //print(UserService.userData!["Friends"]);
    print("0000000000000000000000000000000000000000000000000");
    await FirebaseDatabase.instance.ref("Users").child(friendData["id"]).get().then((snap){
      friendData = jsonDecode(jsonEncode(snap.value));
    });
    print(friendData);
    if(UserService.userData!["Friends"]!=null) {
      UserService.userData!["Friends"].forEach((key,value){
        String name = value["name"].toString();
        String profilePic = value["profilePic"].toString();
        String email = value["email"].toString();
        print(name);
        if(friendData["Friends"]!=null){
          friendData["Friends"].forEach((k,v){
            String targetName = v["name"].toString();
            String targetEmail = v["email"].toString();
            print("\t $targetName");
            if((name==targetName) && (email==targetEmail)){
              //print(name);
              setState(() {
                mutualFriends.add(
                    InkWell(
                      onTap: ()async{
                        if(true){
                          await showMenu(context: context,
                              position: RelativeRect.fromLTRB(100, 100, 100, 100),
                              items: [
                                PopupMenuItem(
                                  child: CircleAvatar(
                                    backgroundColor: Colors.black,
                                    radius: 100,
                                    //maxRadius: 100,// Image radius
                                    backgroundImage: profilePic=="null"?Image.asset("assets/images/profile.png").image:Image.network(profilePic).image,
                                  ),
                                ),
                                PopupMenuItem(
                                    child: Text(name)
                                ),
                                PopupMenuItem(
                                    child: Text(email)
                                )
                              ]
                          );
                        }else{
                          Navigator.of(context).pop();
                        }
                      },
                      child: CircleAvatar(
                        backgroundColor: Colors.black,
                        radius: 30,
                        //maxRadius: 100,// Image radius
                        backgroundImage: profilePic=="null"?Image.asset("assets/images/profile.png").image:Image.network(profilePic).image,
                      ),
                    )
                );
              });
            }
          });
        }else{
          print(null);
        }
      });
    }
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    getMutualFriends();
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text("${friendData["name"].toString()}${S.of(context).Profile}",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),),
      ),
      body: Center(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(14),
              child: CircleAvatar(
                backgroundColor: Colors.black,
                radius: 80,
                //maxRadius: 100,// Image radius
                backgroundImage: friendData["profilePic"].toString()=="null"?Image.asset("assets/images/profile.png").image:Image.network(friendData["profilePic"].toString()).image,
              ),
            ),
            SizedBox(height: 15,),
            Text(S.of(context).UserName,style: TextStyle(color: Colors.green,fontSize: 15),),
            SizedBox(height: 20,),
            Padding(
              padding: const EdgeInsets.fromLTRB(450,0,300,20),
              child: ListTile(
                title: Text(friendData["name"].toString(),style: TextStyle(color: Colors.white,fontSize: 20),),
              ),
            ),
            SizedBox(height: 15,),
            Text(S.of(context).About,style: TextStyle(color: Colors.green,fontSize: 15),),
            Padding(
              padding: const EdgeInsets.fromLTRB(450,0,300,20),
              child: ListTile(
                title: Text(friendData["about"].toString(),style: TextStyle(color: Colors.white,fontSize: 20),),
              ),
            ),
            SizedBox(height: 15,),
            Text(S.of(context).UserEmail,style: TextStyle(color: Colors.green,fontSize: 15),),
            Padding(
              padding: const EdgeInsets.fromLTRB(450,0,300,20),
              child: ListTile(
                title: Text(friendData["email"].toString(),style: TextStyle(color: Colors.white,fontSize: 17),),
              ),
            ),
            SizedBox(height: 15,),
            Text(S.of(context).MutualFriends1,style: TextStyle(color: Colors.green,fontSize: 15),),
            mutualFriends.length>0 ? Padding(
              padding: const EdgeInsets.all(8.0),
              child: SizedBox(
                width: 500,
                child: Stack(
                  children: [
                    Container(
                      width: 300,
                      child: ExpandableMenu(
                        width: 50,
                        height: 50,
                        items: mutualFriends,
                        iconColor: Colors.white,
                        backgroundColor: Colors.white10,
                      ),
                    )
                  ],
                ),
              ),

            ) : Text(S.of(context).MutualFriends2,style: TextStyle(color: Colors.green,fontSize: 17,fontWeight: FontWeight.bold),),
          ],
        ),
      ),
    );
  }
}
