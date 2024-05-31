import 'dart:convert';

import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/home_screen.dart';
import 'package:talk_in_web/presentation/screens/my_group_screen.dart';
import 'package:talk_in_web/services/user_service.dart';
class GroupProfile extends StatefulWidget {
  late Map<String,dynamic> groupData;
  GroupProfile(this.groupData);

  @override
  State<GroupProfile> createState() => _GroupProfileState(this.groupData);
}

class _GroupProfileState extends State<GroupProfile> {
  late Map<String,dynamic> groupData;
  _GroupProfileState(this.groupData);
  bool isEditingName = false;
  bool isEditingDescription = false;
  TextEditingController nameController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();

  int myMemberKey = 0;
  List<Map<String,String>> members=[];
  Future<void> readMembers() async {
    List<String> temp = (groupData["Members"] as List<dynamic>).map((e) => e.toString()).toList();
    for(int i=0;i<temp.length;i++){
      String value = temp[i];
      if(value.toString()==UserService.userData!["id"].toString()){
        setState(() {
          myMemberKey = i;
        });
      }
      await FirebaseDatabase.instance.ref("Users").child(value.toString()).get().then((snap){
        final result = snap.value;
        final data = jsonDecode(jsonEncode(result));
        Map<String,String> mp = Map();
        mp["name"] = data["name"].toString();
        mp["profilePic"] = data["profilePic"].toString();
        if(groupData["Admin"].toString()==data["id"].toString()){
          mp["Admin"]="Admin";
        }
        setState(() {
          members.add(mp);
        });
      });
    }
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    readMembers();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text("Group Setting",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),),
      ),
      body: Center(
        child: SingleChildScrollView(
          scrollDirection: Axis.vertical,
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(12.0),
                child: Text("Group Name",style: TextStyle(color: Colors.green,fontSize: 15),),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(450,0,300,20),
                child: !isEditingName? ListTile(
                  title: Text(groupData["name"].toString(),style: TextStyle(color: Colors.white,fontSize: 20),),
                  trailing: IconButton(
                    onPressed: (){
                      setState(() {
                        nameController.text = groupData["name"].toString();
                        isEditingName = !isEditingName;
                      });
                    },
                    icon: Icon(Icons.edit,color: Colors.white,),
                  ),
                ): ListTile(
                  title: TextFormField(
                    maxLines: 1,
                    maxLength: 40,
                    controller: nameController,
                    cursorColor: Colors.green,
                    style: TextStyle(
                        color: Colors.white
                    ),
                  ),
                  trailing: IconButton(
                    onPressed: () async{
                      setState(() {
                        groupData["name"] = nameController.text;
                        isEditingName = !isEditingName;
                      });
                      await FirebaseDatabase.instance.ref("Groups").child(groupData["id"].toString()).update({
                        "name":nameController.text
                      });
                    },
                    icon: Icon(Icons.check,color: Colors.white,),
                  ),
                ) ,
              ),
              Padding(
                padding: const EdgeInsets.all(12.0),
                child: Text("Group Description",style: TextStyle(color: Colors.green,fontSize: 15),),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(450,0,300,20),
                child: !isEditingDescription? ListTile(
                  title: Text(groupData["groupDescription"].toString(),style: TextStyle(color: Colors.white,fontSize: 20),),
                  trailing: IconButton(
                    onPressed: (){
                      setState(() {
                        descriptionController.text = groupData["groupDescription"].toString();
                        isEditingDescription = !isEditingDescription;
                      });
                    },
                    icon: Icon(Icons.edit,color: Colors.white,),
                  ),
                ): ListTile(
                  title: TextFormField(
                    maxLines: 1,
                    maxLength: 40,
                    controller: descriptionController,
                    cursorColor: Colors.green,
                    style: TextStyle(
                        color: Colors.white
                    ),
                  ),
                  trailing: IconButton(
                    onPressed: () async{
                      if(descriptionController.text.isNotEmpty){
                        setState(() {
                          groupData["groupDescription"] = descriptionController.text;
                          isEditingDescription = !isEditingDescription;
                        });
                        await FirebaseDatabase.instance.ref("Groups").child(groupData["id"].toString()).update({
                          "groupDescription":descriptionController.text
                        });
                      }else{
                        setState(() {
                          groupData["groupDescription"] = "No Description to show";
                          isEditingDescription = !isEditingDescription;
                        });
                        await FirebaseDatabase.instance.ref("Groups").child(groupData["id"].toString()).update({
                          "groupDescription":"No Description to show"
                        });
                      }
                    },
                    icon: Icon(Icons.check,color: Colors.white,),
                  ),
                ) ,
              ),
              Padding(
                padding: const EdgeInsets.all(6.0),
                child: Text("Members",style: TextStyle(color: Colors.green,fontSize: 15),),
              ),
              Container(
                width: 500,
                decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.9),
                    borderRadius: BorderRadius.all(Radius.circular(18))
                ),
                child: ListView.builder(
                    itemCount: members.length,
                    shrinkWrap: true,
                    itemBuilder: (context,index){
                    return ListTile(
                      leading: members[index]["profilePic"].toString()=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(members[index]["profilePic"].toString(),width: 30,height:30,),
                      title: Text(members[index]["name"].toString(),style: TextStyle(color: Colors.black26,),),
                      trailing: members[index]["Admin"].toString()=="null"?Text(""):Text("Creator",style: TextStyle(color: Colors.lightGreen,fontWeight: FontWeight.bold),),
                    );
                }),
              ),
              Padding(
                padding: const EdgeInsets.all(13.0),
                child: TextButton.icon(onPressed: () async{
                    await FirebaseDatabase.instance.ref("Groups").child(groupData["id"].toString()).child("Members").child(myMemberKey.toString()).remove();
                    Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
                      return MyGroupsScreen();
                    }));
                }, label: Text("Exit Group",style: TextStyle(color: Colors.red,fontWeight: FontWeight.bold,fontSize: 18),),icon: Icon(Icons.logout,color: Colors.red,),),
              )
            ],
          ),
        ),
      ),
    );
  }
}
