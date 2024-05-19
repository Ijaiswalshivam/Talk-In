import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/home_screen.dart';
import 'package:talk_in_web/services/data_service.dart';

class AuthService extends ChangeNotifier{

  bool _loading = false;
  bool get loading => _loading;

  void setLoading(bool value){
    _loading = value;
    notifyListeners();
  }

  final auth = FirebaseAuth.instance;

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

  void logOutOfAccount(){

  }

}