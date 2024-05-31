import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/services/auth_service.dart';
import 'package:talk_in_web/generated/l10n.dart';

class SignUpScreen extends StatefulWidget {
  const SignUpScreen({super.key});

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {

  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  bool obscure = true;

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
                height: 400,
                decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.9),
                    borderRadius: BorderRadius.all(Radius.circular(18))
                ),
                child: Column(
                  children: [
                    Center(
                      child: Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Text(S.of(context).SignUp,style: TextStyle(fontSize: 22,fontWeight: FontWeight.bold,color: Colors.black),),
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
                            controller: nameController,
                            decoration: InputDecoration(
                              //labelText: "Enter Name *"
                                icon: Icon(Icons.person),
                                hintText: S.of(context).EnterName,
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
                            maxLines: 1,
                            controller: emailController,
                            decoration: InputDecoration(
                                icon: Icon(Icons.mail),
                                // labelText: "Enter Email *"
                                hintText: S.of(context).EnterEmail,
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
                                hintText: S.of(context).EnterPassword,
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
                              String name = nameController.text;
                              String email = emailController.text;
                              String password = passwordController.text;
                              if(name.isEmpty){
                                ScaffoldMessenger.of(context).showSnackBar(SnackBar(backgroundColor: Colors.black26,content: Text("Name field is required",style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),)));
                                return null;
                              }
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
                              authServiceViewModel.createAnAccount(context,name, email, password);
                            },
                            child: Text(S.of(context).CreateAnAccount,style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.white),),
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
