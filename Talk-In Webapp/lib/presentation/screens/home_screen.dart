import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/presentation/screens/find_people.dart';
import 'package:talk_in_web/presentation/screens/messaging_screen.dart';
import 'package:talk_in_web/presentation/screens/my_group_screen.dart';
import 'package:talk_in_web/presentation/screens/notification_screen.dart';
import 'package:talk_in_web/presentation/screens/settings_screen.dart';
import 'package:talk_in_web/services/user_service.dart';
import 'package:talk_in_web/generated/l10n.dart';
import '../../services/data_service.dart';
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  bool isFriendAdded(List<String> list,Map<String,dynamic> friend){
    for(int i=0;i<list.length;i++){
      if(list[i].toString().compareTo(friend["id"].toString())==0){
        return true;
      }
    }
    return false;
  }

  void createGroup(List<Map<String,dynamic>> friends){
    List<String> list = [];
    showDialog(context: context, builder: (context){
      return StatefulBuilder(
          builder: (context,StateSetter setInnerState){
            TextEditingController groupNameController = TextEditingController();
            return AlertDialog(
              title: Column(
                children: [
                  Text(S.of(context).AddFriends,style: TextStyle(color: Colors.black),),
                  SizedBox(height: 10,),
                  Container(
                    width: MediaQuery.of(context).size.width/2,
                    child: TextField(
                      controller: groupNameController,
                      maxLines: 1,
                      decoration: InputDecoration(
                          hintText: S.of(context).GroupName
                      ),
                    ),
                  )
                ],
              ),
              content: Container(
                height: MediaQuery.of(context).size.height/3,
                width: MediaQuery.of(context).size.width/2,
                child: ListView.builder(
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: friends.length,
                    itemBuilder: (context,index){
                      String name = friends[index]["name"].toString();
                      String profilePic = friends[index]["profilePic"].toString();
                      return ListTile(
                        onTap: (){
                          setInnerState(() {
                            if(!isFriendAdded(list, friends[index])){
                              list.add(friends[index]["id"].toString());
                              //print(list);
                            }else{
                              int ind = -1;
                              for(int i=0;i<list.length;i++){
                                if(list[i].toString().compareTo(friends[index]["id"].toString())==0){
                                  ind = i;
                                  break;
                                }
                              }
                              list.removeAt(ind);
                              //print(list);
                            }
                          });
                        },
                        trailing: isFriendAdded(list, friends[index]) ? Icon(Icons.check_box) : Icon(Icons.check_box_outline_blank),
                        title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.black),),
                        leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
                      );
                    }),
              ),
              actions: [
                TextButton(onPressed: (){Navigator.of(context).pop();}, child: Text(S.of(context).Cancel)),
                TextButton(onPressed: () async{
                  if(list.length>0 && groupNameController.text.isNotEmpty){
                    //print(list.length);
                    final groupRef = FirebaseDatabase.instance.ref("Groups");
                    String? key = groupRef.push().key;
                    if(key!=null){
                      list.add(UserService.userData!["id"].toString());
                      String groupId = key;
                      await groupRef.child(groupId).set({
                        "id":groupId,
                        "name":groupNameController.text,
                        "Members":list,
                        "Admin":UserService.userData!["id"].toString(),
                        "groupDescription":"No Description to show"
                      }).then((value){
                        print("send");
                        Navigator.of(context).pop();
                      });
                    }
                  }
                }, child: Text(S.of(context).Create)),
              ],
            );
          }
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final dataServiceViewModel = Provider.of<DataService>(context);
    Future.delayed(Duration.zero,(){
      dataServiceViewModel.getFriendsList();
      dataServiceViewModel.getRequestsList();
    });
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text("${S.of(context).Hello} ${UserService.userData!["name"]}",style: TextStyle(color: Colors.white),),
        actions: [
          TextButton.icon(onPressed: (){
            Navigator.push(context, MaterialPageRoute(builder: (context){
              return MyGroupsScreen();
            }));
          }
            , label: Text(S.of(context).MyGroups,style: TextStyle(color: Colors.white),),
            icon: Icon(Icons.people_alt,color: Colors.white,),
          ),
          TextButton.icon(onPressed: (){
            // Navigator.push(context, MaterialPageRoute(builder: (context){
            //   return NotificationScreen();
            // }));
            createGroup(dataServiceViewModel.friendList);
          }
            , label: Text(S.of(context).CreateGroup,style: TextStyle(color: Colors.white),),
            icon: Icon(Icons.add,color: Colors.white,),
          ),
          TextButton.icon(onPressed: (){
            Navigator.push(context, MaterialPageRoute(builder: (context){
              return FindPeople();
            }));
          }
            , label: Text(S.of(context).FindPeople,style: TextStyle(color: Colors.white),),
            icon: Icon(Icons.search,color: Colors.white,),
          ),
          TextButton.icon(onPressed: (){
            Navigator.push(context, MaterialPageRoute(builder: (context){
              return NotificationScreen();
            }));
          }
            , label: Text(S.of(context).Notification,style: TextStyle(color: dataServiceViewModel.requestList.length>0? Colors.greenAccent : Colors.white),),
            icon: Icon(Icons.message,color: Colors.white,),
          ),
          IconButton(
              onPressed: (){
                Navigator.push(context, MaterialPageRoute(builder: (context){
                  return SettingsScreen();
                }));
              },
              icon: Icon(Icons.settings,color: Colors.white,)
          ),
          IconButton(
              onPressed: (){

              },
              icon: Icon(Icons.logout,color: Colors.white,)
          ),
        ],
      ),
      body: dataServiceViewModel.friendList.length>0?
      ListView.builder(
          itemCount: dataServiceViewModel.friendList.length,
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemBuilder: (context,index){
            String name = dataServiceViewModel.friendList[index]["name"].toString();
            String profilePic = dataServiceViewModel.friendList[index]["profilePic"].toString();
            return ListTile(
              onTap: (){
                Navigator.push(context, MaterialPageRoute(builder: (context){
                  return MessagingScreen(dataServiceViewModel.friendList[index]);
                }));
              },
              title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
            );

          }):Align(
        alignment: Alignment.center,
        child: Text(S.of(context).NoFriendsToShow,style: TextStyle(color: Colors.lightGreenAccent),),
      ),
    );
  }
}
