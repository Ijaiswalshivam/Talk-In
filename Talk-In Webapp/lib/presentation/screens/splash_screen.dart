import 'package:flutter/material.dart';
import 'package:talk_in_web/presentation/screens/entry_screen.dart';
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with TickerProviderStateMixin {

  late final AnimationController _controller = AnimationController(
    duration: const Duration(seconds: 2),
    vsync: this,
  )..repeat();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    Future.delayed(Duration(seconds: 2),(){
      Navigator.pushReplacement(context, PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) => EntryScreen(),
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          return FadeTransition(
            opacity: animation,
            child: child,
          );
        },
      ));
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              AnimatedBuilder(
              animation: _controller,
              child: Container(
                width: 400.0,
                height: 400.0,
                child: Center(
                  child:Image.asset("assets/images/talkin.jpg",width: 400,height: 400,),
                ),
              ),
              builder: (BuildContext context, Widget? child) {
                return Transform.translate(
                    offset: Offset(0,-150 * _controller.value),
                    child: child,
                );
              },
                    ),
              CircularProgressIndicator(color: Colors.white,)
            ],
          ),
        )
    );
  }
}
