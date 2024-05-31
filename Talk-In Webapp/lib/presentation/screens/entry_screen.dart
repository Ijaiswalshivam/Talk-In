import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/presentation/screens/sign_in_screen.dart';
import 'package:talk_in_web/presentation/screens/signup_screen.dart';
import 'package:talk_in_web/services/auth_service.dart';
import 'package:talk_in_web/generated/l10n.dart';
import 'package:talk_in_web/services/language_service.dart';
class EntryScreen extends StatefulWidget {
  const EntryScreen({super.key});

  @override
  State<EntryScreen> createState() => _EntryScreenState();
}

class _EntryScreenState extends State<EntryScreen> {

  IconData translate = IconData(0xe67b, fontFamily: 'MaterialIcons');

  @override
  Widget build(BuildContext context) {
    final languageServiceProvider = Provider.of<LanguageService>(context);
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
            SingleChildScrollView(
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
                        child: Stack(
                          children: [
                            Positioned(
                                top: 0,
                                child: PopupMenuButton(
                                  icon: Icon(translate,size: 35,),
                                  iconColor: Colors.black,
                                  itemBuilder: (context)=>[
                                    PopupMenuItem(
                                        value:0,
                                        child: Text("English",style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold),)
                                    ),
                                    PopupMenuItem(
                                        value:1,
                                        child: Text("हिंदी",style: TextStyle(color: Colors.black,fontWeight: FontWeight.bold),)
                                    ),
                                  ],
                                  onSelected: (value){
                                    if(value==0){
                                      languageServiceProvider.setLanguageCode("en");
                                      //print(languageService.languageCode);
                                    }
                                    else if(value==1){
                                      languageServiceProvider.setLanguageCode("hi");
                                      //print(languageService.languageCode);
                                    }
                                  },
                                )
                            ),
                            Column(
                              children: [
                                Center(
                                  child: Padding(
                                    padding: const EdgeInsets.all(12.0),
                                    child: Text(S.of(context).Welcome,style: TextStyle(fontSize: 22,fontWeight: FontWeight.bold,color: Colors.black),),
                                  ),
                                ),
                                Center(
                                  child: Padding(
                                    padding: const EdgeInsets.all(5.0),
                                    child: Text(S.of(context).Discover,style: TextStyle(fontSize: 13,fontWeight: FontWeight.bold,color: Colors.grey),),
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
                                        child: Text(S.of(context).SignIn,style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.white),),
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
                                        child: Text(S.of(context).SignUp,style: TextStyle(fontSize: 14,fontWeight: FontWeight.bold,color: Colors.black),),
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
                                    child: Text(S.of(context).Connect,style: TextStyle(fontSize: 13,color: Colors.grey),),
                                  ),
                                ),
                                SizedBox(height: 30,),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    GestureDetector(
                                      onTap: (){
                                        AuthService().GoogleAuthentication(context);
                                      },
                                      child: Padding(
                                        padding: const EdgeInsets.all(12.0),
                                        child: Image.asset("assets/images/google.PNG",width: 30,height: 30,),
                                      ),
                                    ),
                                    GestureDetector(
                                      onTap: (){
                                        AuthService().FacebookAuthentication();
                                      },
                                      child: Padding(
                                        padding: const EdgeInsets.all(12.0),
                                        child: Image.asset("assets/images/facebook_f.PNG",width: 30,height: 30,),
                                      ),
                                    ),
                                    GestureDetector(
                                      onTap: (){
                                        AuthService().TwitterAuthentication();
                                      },
                                      child: Padding(
                                        padding: const EdgeInsets.all(12.0),
                                        child: Image.asset("assets/images/twitter.PNG",width: 30,height: 30,),
                                      ),
                                    ),
                                    GestureDetector(
                                      onTap: (){
                                        AuthService().YahooAuthentication();
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
                          ],
                        )
                    ),
                  )
                ],
              ),
            ),
          ],
        )
    );
  }
}
