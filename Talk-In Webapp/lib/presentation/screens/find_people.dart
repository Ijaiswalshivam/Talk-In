import 'package:flutter/material.dart';
import 'package:talk_in_web/services/data_service.dart';
class FindPeople extends StatefulWidget {
  const FindPeople({super.key});

  @override
  State<FindPeople> createState() => _FindPeopleState();
}

class _FindPeopleState extends State<FindPeople> {

  List<Map<String,dynamic>> userList = [];

  void loadUsers() async{
    final list = await DataService().getAllUsersExceptCurrentUser();
    setState(() {
      userList = list;
    });
  }

  @override
  void initState() {
    super.initState();
    loadUsers();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
      ),
      body: ListView.builder(
          itemCount: userList.length,
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemBuilder: (context,index){
            String name = userList[index]["name"].toString();
            String profilePic = userList[index]["profilePic"].toString();
            return ListTile(
              title: Text(name,style: TextStyle(fontWeight: FontWeight.bold,fontSize: 20,color: Colors.white),),
              leading: profilePic=="null"?Image.asset("assets/images/profile.png",width: 30,height:30,):Image.network(profilePic,width: 30,height:30,),
              trailing: ElevatedButton(
                onPressed: () async{
                  await DataService().sendFriendRequest(context, userList[index]);
                  ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Sent",style: TextStyle(color: Colors.white),)));
                },
                child: Text("Send friend Request"),
              ),
            );
          }),
    );
  }
}
