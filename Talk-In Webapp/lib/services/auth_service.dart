import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/home_screen.dart';
import 'package:talk_in_web/services/data_service.dart';

class AuthService{
  final auth = FirebaseAuth.instance;

  void createAnAccount(BuildContext context,String name,String email,String password) async{
    UserCredential userCredential = await auth.createUserWithEmailAndPassword(email: email, password: password);
    if(userCredential.user!=null){
      bool isUserAdded = await DataService().addUserToFirebase(context,userCredential.user!.uid, name, email, password);
      if(isUserAdded){
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
          return HomeScreen();
        }));
      }else{
        //something went wrong
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      }
    }else{
      //something went wrong
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
    }
  }

  void logIntoAccount(BuildContext context,String email,String password) async{
    UserCredential userCredential = await auth.signInWithEmailAndPassword(email: email, password: password);
    if(userCredential.user!=null){
      await DataService().getUserFromFirebase(context,userCredential.user!.uid);
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
        return HomeScreen();
      }));
    }else{
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
    }
  }

  void logOutOfAccount(){

  }

}