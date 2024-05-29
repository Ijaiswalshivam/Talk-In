import 'dart:async';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:talk_in_web/presentation/screens/home_screen.dart';
import 'package:talk_in_web/services/data_service.dart';
import 'package:http/http.dart' as http;
import 'dart:convert' show json, jsonDecode, jsonEncode;

import 'package:talk_in_web/services/user_service.dart';

class AuthService extends ChangeNotifier{

  bool _loading = false;
  bool get loading => _loading;

  void setLoading(bool value){
    _loading = value;
    notifyListeners();
  }

  final auth = FirebaseAuth.instance;

  // GoogleSignIn _googleSignIn = GoogleSignIn(
  //   clientId: '150306616826-4brf4ovgpu8c0vu3ritqpppthd3adnbu.apps.googleusercontent.com',
  //   scopes: [
  //     'email',
  //     'https://www.googleapis.com/auth/contacts.readonly',
  //   ],
  // );

  void createAnAccount(BuildContext context,String name,String email,String password) async{
    setLoading(true);
    UserCredential userCredential = await auth.createUserWithEmailAndPassword(email: email, password: password);
    if(userCredential.user!=null){
      bool isUserAdded = await DataService().addUserToFirebase(context,userCredential.user!.uid, name, email, password);
      if(isUserAdded){
        setLoading(false);
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
          return HomeScreen();
        }));
      }else{
        setLoading(false);
        //something went wrong
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      }
    }else{
      setLoading(false);
      //something went wrong
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
    }
  }

  void logIntoAccount(BuildContext context,String email,String password) async{
    setLoading(true);
    try {
      UserCredential userCredential = await auth.signInWithEmailAndPassword(email: email, password: password);
      print(userCredential);
      if(userCredential.user!=null){
        await DataService().getUserFromFirebase(context,userCredential.user!.uid);
        setLoading(false);
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
          return HomeScreen();
        }));
      }else{
        setLoading(false);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong",style: TextStyle(color: Colors.white),)));
      }
    } on Exception catch (e) {
      // TODO
      print(e);
      setLoading(false);
    }
  }

  void GoogleAuthentication(BuildContext context) async{
    try{
      setLoading(true);
      GoogleAuthProvider authProvider = GoogleAuthProvider();
      try {
        final UserCredential userCredential = await auth.signInWithPopup(authProvider);
        User? user = userCredential.user;
        print(user);
        if(user!=null){
          final datasnap = await FirebaseDatabase.instance.ref("Users").child(user!.uid).get();
          final data = datasnap.value;
          if(data!=null){
            print("Already a user");
            print(data);
            final result = jsonDecode(jsonEncode(data));
            UserService.userData = result;
            print(UserService.userData);
            setLoading(false);
            Navigator.pushReplacement(context, MaterialPageRoute(builder: (context){
              return HomeScreen();
            }));
          }else{
            bool isUserAdded = await DataService().addUserToFirebase(context,user!.uid, user!.displayName.toString(), user!.email.toString(), "123456");
            if(isUserAdded) {
              setLoading(false);
              Navigator.pushReplacement(
                  context, MaterialPageRoute(builder: (context) {
                return HomeScreen();
              }));
            }
          }
        }
        setLoading(false);
      } catch (e) {
        setLoading(false);
        print(e);
      }
    }catch(e){
      print("google error $e");
      setLoading(false);
    }
  }

  void FacebookAuthentication() async{
    try{
      setLoading(true);
      FacebookAuthProvider authProvider = FacebookAuthProvider();
      try {
        final UserCredential userCredential = await auth.signInWithPopup(authProvider);
        User? user = userCredential.user;
        print(user);
        setLoading(false);
      } catch (e) {
        setLoading(false);
        print(e);
      }
    }catch(e){
      print("facebook error $e");
      setLoading(false);
    }
  }

  void TwitterAuthentication() async{
    try{
      setLoading(true);
      TwitterAuthProvider authProvider = TwitterAuthProvider();
      try {
        final UserCredential userCredential = await auth.signInWithPopup(authProvider);
        User? user = userCredential.user;
        print(user);
        setLoading(false);
      } catch (e) {
        setLoading(false);
        print(e);
      }
    }catch(e){
      print("facebook error $e");
      setLoading(false);
    }
  }

  void YahooAuthentication() async{
    try{
      setLoading(true);
      YahooAuthProvider authProvider = YahooAuthProvider();
      try {
        final UserCredential userCredential = await auth.signInWithPopup(authProvider);
        User? user = userCredential.user;
        print(user);
        setLoading(false);
      } catch (e) {
        setLoading(false);
        print(e);
      }
    }catch(e){
      print("facebook error $e");
      setLoading(false);
    }
  }

  void logOutOfAccount(){

  }

  void forgotPassword( BuildContext context,String email) async{
    try{
      await auth.sendPasswordResetEmail(email: email).then((value){
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Email successfully sent",style: TextStyle(color: Colors.white),),duration: Duration(seconds: 4),));
      });
    }catch(e){
      print(e);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Something went wrong or the entered email is wrong",style: TextStyle(color: Colors.white),),duration: Duration(seconds: 3),));
    }
  }

}