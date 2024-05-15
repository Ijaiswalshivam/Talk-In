import 'package:flutter/material.dart';

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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
                                return null;
                              }
                              if(password.isEmpty){
                                return null;
                              }
                              if(password.length<8){
                                return null;
                              }
                              AuthService().logIntoAccount(context,email, password);

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
