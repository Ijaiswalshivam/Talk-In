import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/presentation/screens/find_people.dart';
import 'package:talk_in_web/presentation/screens/messaging_screen.dart';
import 'package:talk_in_web/presentation/screens/notification_screen.dart';
import 'package:talk_in_web/services/user_service.dart';

import '../../services/data_service.dart';
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {


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
        title: Text("Hello ${UserService.userData!["name"]}",style: TextStyle(color: Colors.white),),
        actions: [
          TextButton.icon(onPressed: (){
            Navigator.push(context, MaterialPageRoute(builder: (context){
              return FindPeople();
            }));
          }
            , label: Text("Find People",style: TextStyle(color: Colors.white),),
            icon: Icon(Icons.search,color: Colors.white,),
          ),
          TextButton.icon(onPressed: (){
            Navigator.push(context, MaterialPageRoute(builder: (context){
              return NotificationScreen();
            }));
          }
            , label: Text("Notification",style: TextStyle(color: dataServiceViewModel.requestList.length>0? Colors.greenAccent : Colors.white),),
            icon: Icon(Icons.message,color: Colors.white,),
          ),
          IconButton(
              onPressed: (){

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
        child: Text("No friends to show. Go and find some people, you introvert!!",style: TextStyle(color: Colors.lightGreenAccent),),
      ),
    );
  }
}
