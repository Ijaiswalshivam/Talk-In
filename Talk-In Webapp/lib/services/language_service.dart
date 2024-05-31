import 'package:flutter/cupertino.dart';

class LanguageService extends ChangeNotifier{
  String _languageCode = "en";
  String get languageCode => _languageCode;
  setLanguageCode(String code){
    _languageCode = code;
    notifyListeners();
  }
}