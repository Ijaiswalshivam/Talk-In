import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/sign_in_screen.dart';
import 'package:talk_in_web/presentation/screens/signup_screen.dart';
class EntryScreen extends StatefulWidget {
  const EntryScreen({super.key});

  @override
  State<EntryScreen> createState() => _EntryScreenState();
}

class _EntryScreenState extends State<EntryScreen> {

  Widget loadScreen() {
    return SingleChildScrollView(
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
                      padding: const EdgeInsets.all(12.0),
                      child: Text("Welcome to TalkIn",style: TextStyle(fontSize: 22,fontWeight: FontWeight.bold,color: Colors.black),),
                    ),
                  ),
                  Center(
                    child: Padding(
                      padding: const EdgeInsets.all(5.0),
                      child: Text("Discover amazing people around you",style: TextStyle(fontSize: 13,fontWeight: FontWeight.bold,color: Colors.grey),),
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(18.0),
                    child: SizedBox(
                      width: MediaQuery.of(context).size.width,
                      height: 40,
                      child: ElevatedButton(
                          onPressed: (){
                            Navigator.push(context, MaterialPageRoute(builder: (context){
                              return SignInScreen();
                            }));
                          },
                          child: Text("Sign In",style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.white),),
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
                  Padding(
                    padding: const EdgeInsets.all(18.0),
                    child: SizedBox(
                      width: MediaQuery.of(context).size.width,
                      height: 40,
                      child: ElevatedButton(
                          onPressed: (){
                              Navigator.push(context, MaterialPageRoute(builder: (context){
                                return SignUpScreen();
                              }));
                          },
                          child: Text("Sign Up",style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.black),),
                          style: ButtonStyle(
                              foregroundColor: MaterialStateProperty.all<Color>(Colors.black),
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
                  Padding(
                    padding: const EdgeInsets.fromLTRB(40,5,40,0),
                    child: Divider(
                      color: Colors.black,
                    ),
                  ),
                  Center(
                    child: Padding(
                      padding: const EdgeInsets.all(0.0),
                      child: Text("Or connect using",style: TextStyle(fontSize: 13,color: Colors.grey),),
                    ),
                  ),
                  SizedBox(height: 30,),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      GestureDetector(
                        onTap: (){

                        },
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: Image.asset("assets/images/google.PNG",width: 30,height: 30,),
                        ),
                      ),
                      GestureDetector(
                        onTap: (){

                        },
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: Image.asset("assets/images/facebook_f.PNG",width: 30,height: 30,),
                        ),
                      ),
                      GestureDetector(
                        onTap: (){

                        },
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: Image.asset("assets/images/twitter.PNG",width: 30,height: 30,),
                        ),
                      ),
                      GestureDetector(
                        onTap: (){

                        },
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: Image.asset("assets/images/yahoo.PNG",width: 30,height: 30,),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: Stack(
        children:[

      Center(
        child: SizedBox(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Image.asset("assets/videos/bg.gif"),
            ),
      ),
          loadScreen()
        ],
      )
    );
  }
}
