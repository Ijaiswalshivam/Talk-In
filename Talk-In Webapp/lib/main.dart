import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:talk_in_web/presentation/screens/entry_screen.dart';
import 'package:talk_in_web/services/auth_service.dart';
import 'package:talk_in_web/services/data_service.dart';
import 'package:talk_in_web/services/language_service.dart';
import 'firebase_options.dart';
import 'generated/l10n.dart';
import 'package:flutter_localization/flutter_localization.dart' as localization;

void main() async{
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  //Provider.debugCheckInvalidValueType = null;
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_){
          return AuthService();
        }),
        ChangeNotifierProvider(create: (_){
          return DataService();
        }),
        ChangeNotifierProvider(create: (_){
          return LanguageService();
        }),
      ],
      child: ChangeNotifierProvider<LanguageService>(
        create: (_)=>LanguageService(),
        child: Consumer<LanguageService>(
          builder: (context,provider,child){
            print(context.watch<LanguageService>().languageCode);
            return MaterialApp(
              title: 'TalkIn',
              debugShowCheckedModeBanner: false,
              theme: ThemeData(
                // This is the theme of your application.
                colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
                useMaterial3: true,
              ),
              supportedLocales: [
                Locale('en'),
                Locale('hi')
              ],
              locale: Locale(provider.languageCode),
              localizationsDelegates:[
                AppLocalizationDelegate(),
                ...localization.FlutterLocalization.instance.localizationsDelegates
              ],
              home: EntryScreen(),
            );
          },
        ),
      ),
    );
  }
}
