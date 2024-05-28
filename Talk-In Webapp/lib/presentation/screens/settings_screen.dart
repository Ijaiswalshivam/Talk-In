import 'dart:convert';
import 'dart:html';
import 'dart:io' as io;
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/services/data_service.dart';
import 'package:talk_in_web/services/user_service.dart';
class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {

  TextEditingController nameController = TextEditingController();
  TextEditingController aboutController = TextEditingController();

  bool isEditingName = false;
  bool isEditingAbout = false;

  String profilePic = UserService.userData!["profilePic"].toString();
  String name = UserService.userData!["name"].toString();
  String about = UserService.userData!["about"].toString();

  bool isLoading = false;

  Future<void> uploadProfilePic() async{
    try{
      FileUploadInputElement uploadInput = FileUploadInputElement();
      uploadInput.multiple = false;
      uploadInput.accept = 'image/*';
      uploadInput.click();

      uploadInput.onChange.listen((e) {
        // read file content as dataURL
        final files = uploadInput.files;
        if (files!.length == 1) {
          final file = files[0];
          final reader = new FileReader();

          reader.onLoadEnd.listen((e) async{
            print(e);
            print('loaded: ${file.name}');
            print('type: ${reader.result.runtimeType}');
            print('file size = ${file.size}');
            //print(reader.result);

            if((file.size/1024)/1024<=10){
              final encoded = reader.result.toString();
              // String imageBase64 = encoded.replaceFirst("data:image/png;base64,", "");
              // imageBase64 = encoded.replaceFirst("data:image/jpg;base64,", "");
              // imageBase64 = encoded.replaceFirst("data:image/jpeg;base64,", "");
              //print(imageBase64);// this is to remove some non necessary stuff
              final base64d = base64.decode(encoded.split(',').last);
              print(base64d);
              // io.File _itemPicIoFile = io.File.fromRawPath(base64);
              // print(_itemPicIoFile);

              await FirebaseStorage.instance.ref("ProfilePictures")
                  .child(UserService.userData!["id"])
                  .child("profile.jpg")
                  .putData(base64d,SettableMetadata(contentType: "image/jpg"))
                  .then((taskSnapshot) async{

                String url = await taskSnapshot.ref.getDownloadURL();
                await FirebaseDatabase.instance.ref("Users").child(UserService.userData!["id"]).update({
                  "profilePic":url
                });

                UserService.userData!["profilePic"] = url;

                setState(() {
                  isLoading = false;
                  profilePic = UserService.userData!["profilePic"].toString();
                  print(profilePic);
                });

              });

            }else{
              setState(() {
                isLoading = false;
              });
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("File Size exceeds 10 MB limit",style: TextStyle(color: Colors.white),)));
            }

          });
          reader.readAsDataUrl(file);
        }else{
          setState(() {
            isLoading = false;
          });
        }
      });
    }catch(e){

      setState(() {
        isLoading = false;
      });

      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    // String profilePic = UserService.userData!["profilePic"].toString();
    //final dataServiceViewModel = Provider.of<DataService>(context);
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black26,
        title: Text("Your Profile",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),),
      ),
      body: Center(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(14),
              child: CircleAvatar(
                backgroundColor: Colors.black,
                radius: 100,
                //maxRadius: 100,// Image radius
                backgroundImage: profilePic=="null"?Image.asset("assets/images/profile.png").image:Image.network(profilePic).image,
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(onPressed: () async{

                  await FirebaseDatabase.instance.ref("Users").child(UserService.userData!["id"]).update({
                    "profileVisibility":(!bool.parse(UserService.userData!["profileVisibility"].toString())).toString()
                  });

                  setState(() {
                    UserService.userData!["profileVisibility"] = (!bool.parse(UserService.userData!["profileVisibility"].toString())).toString();
                  });

                }, icon: Icon( bool.parse(UserService.userData!["profileVisibility"].toString())? Icons.visibility : Icons.visibility_off,color: Colors.white,),splashColor: Colors.red,),
                IconButton(onPressed: () async{

                  setState(() {
                    isLoading = true;
                  });

                  await uploadProfilePic();

                }, icon: Icon(Icons.edit,color: Colors.white,),splashColor: Colors.red,),
              ],
            ),
            SizedBox(height: 20,),
            Text("Your Name",style: TextStyle(color: Colors.green,fontSize: 15),),
            SizedBox(height: 30,),
            Padding(
              padding: const EdgeInsets.fromLTRB(450,0,300,20),
              child: !isEditingName? ListTile(
                title: Text(name,style: TextStyle(color: Colors.white,fontSize: 20),),
                trailing: IconButton(
                  onPressed: (){
                    setState(() {
                      nameController.text = name;
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
                      name = nameController.text;
                      isEditingName = !isEditingName;
                    });
                    await FirebaseDatabase.instance.ref("Users").child(UserService.userData!["id"]).update({
                      "name":name
                    });

                    UserService.userData!["name"] = name;

                  },
                  icon: Icon(Icons.check,color: Colors.white,),
                ),
              ) ,
            ),
            Text("This name will be visible to other users",style: TextStyle(color: Colors.grey,fontSize: 10),),
            SizedBox(height: 20,),
            Text("About",style: TextStyle(color: Colors.green,fontSize: 15),),
            Padding(
              padding: const EdgeInsets.fromLTRB(450,0,300,20),
              child: !isEditingAbout? ListTile(
                title: Text(about,style: TextStyle(color: Colors.white,fontSize: 20),),
                trailing: IconButton(
                  onPressed: (){
                    setState(() {
                      aboutController.text = about;
                      isEditingAbout = !isEditingAbout;
                    });
                  },
                  icon: Icon(Icons.edit,color: Colors.white,),
                ),
              ) : ListTile(
                title: TextFormField(
                  maxLines: 2,
                  maxLength: 90,
                  controller: aboutController,
                  cursorColor: Colors.green,
                  style: TextStyle(
                      color: Colors.white
                  ),
                ),
                trailing: IconButton(
                  onPressed: () async{
                    setState(() {
                      about = aboutController.text;
                      isEditingAbout = !isEditingAbout;
                    });
                    await FirebaseDatabase.instance.ref("Users").child(UserService.userData!["id"]).update({
                      "about":about.length>0?about:"Hey! I am using TalkIn"
                    });

                    UserService.userData!["about"] = about;

                  },
                  icon: Icon(Icons.check,color: Colors.white,),
                ),
              ) ,
            ),
            isLoading?CircularProgressIndicator(backgroundColor: Colors.black,color:Colors.white,):Container()
          ],
        ),
      ),
    );
  }
}
