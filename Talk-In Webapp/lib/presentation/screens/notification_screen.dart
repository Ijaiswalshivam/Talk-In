import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../services/data_service.dart';
class NotificationScreen extends StatefulWidget {
  const NotificationScreen({super.key});

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> {

  @override
  Widget build(BuildContext context) {
    final dataServiceViewModel = Provider.of<DataService>(context);
    Future.delayed(Duration.zero,(){
      dataServiceViewModel.getRequestsList();
    });
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
      ),
      body: dataServiceViewModel.requestList.length>0?ListView.builder(
          itemCount: dataServiceViewModel.requestList.length,
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemBuilder: (context,index){
            String name = dataServiceViewModel.requestList[index]["name"].toString();
            String profilePic = dataServiceViewModel.requestList[index]["profilePic"].toString();
            return ListTile(
              title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
              trailing: ElevatedButton(
                onPressed: () async{
                  await dataServiceViewModel.acceptFriendRequest(context, dataServiceViewModel.requestList[index]);
                  ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Accepted",style: TextStyle(color: Colors.white),)));
                },
                child: Text("Accept friend Request"),
              ),
            );
          }):Align(
        alignment: Alignment.center,
        child: Text("No Notifications to show",style: TextStyle(color: Colors.lightGreenAccent),),
      ),
    );
  }
}
