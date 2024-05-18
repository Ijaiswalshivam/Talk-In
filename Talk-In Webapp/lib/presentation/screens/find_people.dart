import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/services/data_service.dart';
class FindPeople extends StatefulWidget {
  const FindPeople({super.key});

  @override
  State<FindPeople> createState() => _FindPeopleState();
}

class _FindPeopleState extends State<FindPeople> {

  //List<Map<String,dynamic>> userList = [];

  // void loadUsers() async{
  //   final list = await DataService().getAllUsersExceptCurrentUser();
  //   setState(() {
  //     userList = list;
  //   });
  // }
  //
  // @override
  // void initState() {
  //   super.initState();
  //   loadUsers();
  // }

  @override
  Widget build(BuildContext context) {
    final dataServiceViewModel = Provider.of<DataService>(context);
    Future.delayed(Duration.zero,() async{
      dataServiceViewModel.getAllUsersExceptCurrentUser();
      // dataServiceViewModel.getFriendsList();
      // dataServiceViewModel.getMySentRequests();
    });
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
      ),
      body: ListView.builder(
          itemCount: dataServiceViewModel.userList.length,
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemBuilder: (context,index){
            String name = dataServiceViewModel.userList[index]["name"].toString();
            String profilePic = dataServiceViewModel.userList[index]["profilePic"].toString();
            // bool isFriend = dataServiceViewModel.friendList.contains(dataServiceViewModel.userList[index]);
            // bool isRequested = dataServiceViewModel.sentRequestList.contains(dataServiceViewModel.userList[index]);
            bool isFriend = false;
            bool isRequested = false;
            dataServiceViewModel.friendList.forEach((element) {
              if(element["id"].toString().compareTo(dataServiceViewModel.userList[index]["id"].toString())==0){
                isFriend = true;
              }
            });
            dataServiceViewModel.sentRequestList.forEach((element) {
              if(element["id"].toString().compareTo(dataServiceViewModel.userList[index]["id"].toString())==0){
                isRequested = true;
              }
            });
            print(dataServiceViewModel.friendList.length);
            print(dataServiceViewModel.sentRequestList.length);
            print("Find People $isFriend $isRequested");
            return ListTile(
              title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
              trailing: ElevatedButton(
                onPressed: isRequested? null: isFriend? (){

                } : () async{
                  await dataServiceViewModel.sendFriendRequest(context, dataServiceViewModel.userList[index]);
                  ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Sent",style: TextStyle(color: Colors.white),)));
                },
                child: isRequested ? Text("Requested",style: TextStyle(color: Colors.white),) : isFriend ? Text("UnFriend") : Text("Send friend Request"),
              ),
            );
          }),
    );
  }
}
