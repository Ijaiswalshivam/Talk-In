import 'package:flutter/material.dart';

import '../../services/data_service.dart';
class NotificationScreen extends StatefulWidget {
  const NotificationScreen({super.key});

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> {

  List<Map<String,dynamic>> requestList = [];

  void loadRequests() async{
    final list = await DataService().getRequestsList();
    setState(() {
      requestList = list;
    });
  }

  @override
  void initState() {
    super.initState();
    loadRequests();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
      ),
      body: ListView.builder(
          itemCount: requestList.length,
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemBuilder: (context,index){
            String name = requestList[index]["name"].toString();
            String profilePic = requestList[index]["profilePic"].toString();
            return ListTile(
              title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
              trailing: ElevatedButton(
                onPressed: () async{
                  await DataService().acceptFriendRequest(context, requestList[index]);
                  ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Accepted",style: TextStyle(color: Colors.white),)));
                  setState(() {
                    requestList.removeAt(index);
                  });
                },
                child: Text("Accept friend Request"),
              ),
            );
          }),
    );
  }
}
