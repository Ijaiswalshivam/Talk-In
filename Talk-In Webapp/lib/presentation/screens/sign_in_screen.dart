import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../services/auth_service.dart';
class SignInScreen extends StatefulWidget {
  const SignInScreen({super.key});

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen> {

  TextEditingController emailController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  bool obscure = true;

  AlertDialog showTheDialog(){
    TextEditingController control = TextEditingController();
    return AlertDialog(
      title: Text("Forgot Password",style: TextStyle(color: Colors.black),),
      content: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Container(
          width: MediaQuery.of(context).size.width/3,
          height: 50,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(10),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.5),
                spreadRadius: 5,
                blurRadius: 7,
                offset: Offset(0, 3), // changes position of shadow
              ),
            ],
          ),
          child: Padding(
            padding: const EdgeInsets.all(12.0),
            child: TextFormField(
              maxLines: 1,
              controller: control,
              decoration: InputDecoration(
                  icon: Icon(Icons.mail),
                  // labelText: "Enter Email *"
                  hintText: "Enter Your Account Email",
                  border: InputBorder.none
              ),
            ),
          ),
        ),
      ),
      actions: [
        TextButton(onPressed: (){Navigator.of(context).pop();}, child: Text("NO")),
        TextButton(onPressed: (){
          if(control.text.isNotEmpty){
            Navigator.of(context).pop();
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Check your email inbox",style: TextStyle(color: Colors.white),)));
            AuthService().forgotPassword(context, control.text);
          }else{
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Email field is required",style: TextStyle(color: Colors.white),)));
          }
        }, child: Text("Yes")),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final authServiceViewModel = Provider.of<AuthService>(context);
    return authServiceViewModel.loading?Scaffold(
      backgroundColor: Colors.black,
      body: Align(
        alignment: Alignment.center,
        child: CircularProgressIndicator(
          color: Colors.white,backgroundColor: Colors.black,
        ),
      ),
    ):Scaffold(
      backgroundColor: Colors.black,
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(12.0),
              child: Center(child: Image.asset("assets/images/talkin.jpg",width: 350,height: 300,)),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(18,0,18,20),
              child: Container(
                width: 700,
                height: 350,
                decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.9),
                    borderRadius: BorderRadius.all(Radius.circular(18))
                ),
                child: Column(
                  children: [
                    Center(
                      child: Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Text("Sign In",style: TextStyle(fontSize: 22,fontWeight: FontWeight.bold,color: Colors.black),),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Container(
                        width: MediaQuery.of(context).size.width,
                        height: 50,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          color: Colors.white,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.withOpacity(0.5),
                              spreadRadius: 5,
                              blurRadius: 7,
                              offset: Offset(0, 3), // changes position of shadow
                            ),
                          ],
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: TextFormField(
                            maxLines: 1,
                            controller: emailController,
                            decoration: InputDecoration(
                                icon: Icon(Icons.mail),
                                // labelText: "Enter Email *"
                                hintText: "Enter Email",
                                border: InputBorder.none
                            ),
                          ),
                        ),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Container(
                        width: MediaQuery.of(context).size.width,
                        height: 50,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          color: Colors.white,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.withOpacity(0.5),
                              spreadRadius: 5,
                              blurRadius: 7,
                              offset: Offset(0, 3), // changes position of shadow
                            ),
                          ],
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: TextFormField(
                            controller: passwordController,
                            obscureText: obscure,
                            maxLines: 1,
                            decoration: InputDecoration(
                                icon: Icon(Icons.security),
                                suffixIcon:IconButton(
                                  onPressed: (){
                                    setState(() {
                                      obscure = !obscure;
                                    });
                                  },
                                  icon: Icon(obscure?Icons.remove_red_eye_rounded:Icons.remove_red_eye_outlined),
                                ),
                                // labelText: "Enter Password *",
                                hintText: "Enter Password",
                                border: InputBorder.none
                            ),
                          ),
                        ),
                      ),
                    ),
                    Align(
                      alignment: FractionalOffset.bottomRight,
                      child: TextButton(onPressed: (){
                        showDialog(context: context, builder: (context){
                          return showTheDialog();
                        });
                      }, child: Text("Forgot Password?",style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold,fontSize: 20),)),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(12.0),
                      child: SizedBox(
                        width: MediaQuery.of(context).size.width,
                        height: 40,
                        child: ElevatedButton(
                            onPressed: (){

                              String email = emailController.text;
                              String password = passwordController.text;
                              if(email.isEmpty){
                                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Email field is required",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),)));
                                return null;
                              }
                              if(password.isEmpty){
                                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Password field is required",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),)));
                                return null;
                              }
                              if(password.length<8){
                                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Password should be 8 characters long",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),)));
                                return null;
                              }
                              authServiceViewModel.logIntoAccount(context,email, password);

                            },
                            child: Text("Log Into Account",style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.white),),
                            style: ButtonStyle(
                                backgroundColor: MaterialStateProperty.all<Color>(Colors.black),
                                shape: MaterialStateProperty.all<RoundedRectangleBorder>(
                                    RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(10.0),
                                      side: BorderSide(color: Colors.red,),
                                    )
                                )
                            )
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
