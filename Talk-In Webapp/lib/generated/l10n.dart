// GENERATED CODE - DO NOT MODIFY BY HAND
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'intl/messages_all.dart';

// **************************************************************************
// Generator: Flutter Intl IDE plugin
// Made by Localizely
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, lines_longer_than_80_chars
// ignore_for_file: join_return_with_assignment, prefer_final_in_for_each
// ignore_for_file: avoid_redundant_argument_values, avoid_escaping_inner_quotes

class S {
  S();

  static S? _current;

  static S get current {
    assert(_current != null,
        'No instance of S was loaded. Try to initialize the S delegate before accessing S.current.');
    return _current!;
  }

  static const AppLocalizationDelegate delegate = AppLocalizationDelegate();

  static Future<S> load(Locale locale) {
    final name = (locale.countryCode?.isEmpty ?? false)
        ? locale.languageCode
        : locale.toString();
    final localeName = Intl.canonicalizedLocale(name);
    return initializeMessages(localeName).then((_) {
      Intl.defaultLocale = localeName;
      final instance = S();
      S._current = instance;

      return instance;
    });
  }

  static S of(BuildContext context) {
    final instance = S.maybeOf(context);
    assert(instance != null,
        'No instance of S present in the widget tree. Did you add S.delegate in localizationsDelegates?');
    return instance!;
  }

  static S? maybeOf(BuildContext context) {
    return Localizations.of<S>(context, S);
  }

  /// `Welcome to TalkIn`
  String get Welcome {
    return Intl.message(
      'Welcome to TalkIn',
      name: 'Welcome',
      desc: '',
      args: [],
    );
  }

  /// `Discover amazing people around you`
  String get Discover {
    return Intl.message(
      'Discover amazing people around you',
      name: 'Discover',
      desc: '',
      args: [],
    );
  }

  /// `Sign In`
  String get SignIn {
    return Intl.message(
      'Sign In',
      name: 'SignIn',
      desc: '',
      args: [],
    );
  }

  /// `Sign Up`
  String get SignUp {
    return Intl.message(
      'Sign Up',
      name: 'SignUp',
      desc: '',
      args: [],
    );
  }

  /// `Or connect using`
  String get Connect {
    return Intl.message(
      'Or connect using',
      name: 'Connect',
      desc: '',
      args: [],
    );
  }

  /// `Requested`
  String get Requested {
    return Intl.message(
      'Requested',
      name: 'Requested',
      desc: '',
      args: [],
    );
  }

  /// `Un-Friend`
  String get UnFriend {
    return Intl.message(
      'Un-Friend',
      name: 'UnFriend',
      desc: '',
      args: [],
    );
  }

  /// `Send Friend Request`
  String get SendFriendRequest {
    return Intl.message(
      'Send Friend Request',
      name: 'SendFriendRequest',
      desc: '',
      args: [],
    );
  }

  /// `Forgot Password?`
  String get ForgotPassword {
    return Intl.message(
      'Forgot Password?',
      name: 'ForgotPassword',
      desc: '',
      args: [],
    );
  }

  /// `Enter Password`
  String get EnterPassword {
    return Intl.message(
      'Enter Password',
      name: 'EnterPassword',
      desc: '',
      args: [],
    );
  }

  /// `Enter Name`
  String get EnterName {
    return Intl.message(
      'Enter Name',
      name: 'EnterName',
      desc: '',
      args: [],
    );
  }

  /// `Enter Email`
  String get EnterEmail {
    return Intl.message(
      'Enter Email',
      name: 'EnterEmail',
      desc: '',
      args: [],
    );
  }

  /// `No`
  String get No {
    return Intl.message(
      'No',
      name: 'No',
      desc: '',
      args: [],
    );
  }

  /// `Yes`
  String get Yes {
    return Intl.message(
      'Yes',
      name: 'Yes',
      desc: '',
      args: [],
    );
  }

  /// `Log Into Account`
  String get LogIntoAccount {
    return Intl.message(
      'Log Into Account',
      name: 'LogIntoAccount',
      desc: '',
      args: [],
    );
  }

  /// `Create An Account`
  String get CreateAnAccount {
    return Intl.message(
      'Create An Account',
      name: 'CreateAnAccount',
      desc: '',
      args: [],
    );
  }

  /// `Add Friends`
  String get AddFriends {
    return Intl.message(
      'Add Friends',
      name: 'AddFriends',
      desc: '',
      args: [],
    );
  }

  /// `Enter a group name`
  String get GroupName {
    return Intl.message(
      'Enter a group name',
      name: 'GroupName',
      desc: '',
      args: [],
    );
  }

  /// `Create`
  String get Create {
    return Intl.message(
      'Create',
      name: 'Create',
      desc: '',
      args: [],
    );
  }

  /// `Cancel`
  String get Cancel {
    return Intl.message(
      'Cancel',
      name: 'Cancel',
      desc: '',
      args: [],
    );
  }

  /// `Hello`
  String get Hello {
    return Intl.message(
      'Hello',
      name: 'Hello',
      desc: '',
      args: [],
    );
  }

  /// `My Groups`
  String get MyGroups {
    return Intl.message(
      'My Groups',
      name: 'MyGroups',
      desc: '',
      args: [],
    );
  }

  /// `Create Group`
  String get CreateGroup {
    return Intl.message(
      'Create Group',
      name: 'CreateGroup',
      desc: '',
      args: [],
    );
  }

  /// `Find People`
  String get FindPeople {
    return Intl.message(
      'Find People',
      name: 'FindPeople',
      desc: '',
      args: [],
    );
  }

  /// `Notification`
  String get Notification {
    return Intl.message(
      'Notification',
      name: 'Notification',
      desc: '',
      args: [],
    );
  }

  /// `No friends to show. Go and find some people, you introvert!!`
  String get NoFriendsToShow {
    return Intl.message(
      'No friends to show. Go and find some people, you introvert!!',
      name: 'NoFriendsToShow',
      desc: '',
      args: [],
    );
  }

  /// `Your Profile`
  String get YourProfile {
    return Intl.message(
      'Your Profile',
      name: 'YourProfile',
      desc: '',
      args: [],
    );
  }

  /// `Your Name`
  String get YourName {
    return Intl.message(
      'Your Name',
      name: 'YourName',
      desc: '',
      args: [],
    );
  }

  /// `This name will be visible to other users`
  String get NameVisibility {
    return Intl.message(
      'This name will be visible to other users',
      name: 'NameVisibility',
      desc: '',
      args: [],
    );
  }

  /// `About`
  String get About {
    return Intl.message(
      'About',
      name: 'About',
      desc: '',
      args: [],
    );
  }

  /// `Hey! I am using TalkIn`
  String get HeyTalkIn {
    return Intl.message(
      'Hey! I am using TalkIn',
      name: 'HeyTalkIn',
      desc: '',
      args: [],
    );
  }

  /// `File Size exceeds 10 MB limit`
  String get FileSize {
    return Intl.message(
      'File Size exceeds 10 MB limit',
      name: 'FileSize',
      desc: '',
      args: [],
    );
  }

  /// `Accept Friend Request`
  String get AcceptRequest {
    return Intl.message(
      'Accept Friend Request',
      name: 'AcceptRequest',
      desc: '',
      args: [],
    );
  }

  /// `No Notifications to show`
  String get NoNotification {
    return Intl.message(
      'No Notifications to show',
      name: 'NoNotification',
      desc: '',
      args: [],
    );
  }

  /// `Type A Message`
  String get TypeMessage1 {
    return Intl.message(
      'Type A Message',
      name: 'TypeMessage1',
      desc: '',
      args: [],
    );
  }

  /// `Wanna Type A Message`
  String get TypeMessage2 {
    return Intl.message(
      'Wanna Type A Message',
      name: 'TypeMessage2',
      desc: '',
      args: [],
    );
  }

  /// `Images`
  String get Images {
    return Intl.message(
      'Images',
      name: 'Images',
      desc: '',
      args: [],
    );
  }

  /// `Document`
  String get Document {
    return Intl.message(
      'Document',
      name: 'Document',
      desc: '',
      args: [],
    );
  }

  /// `Audio`
  String get Audio {
    return Intl.message(
      'Audio',
      name: 'Audio',
      desc: '',
      args: [],
    );
  }

  /// `'s profile`
  String get Profile {
    return Intl.message(
      '\'s profile',
      name: 'Profile',
      desc: '',
      args: [],
    );
  }

  /// `User Name`
  String get UserName {
    return Intl.message(
      'User Name',
      name: 'UserName',
      desc: '',
      args: [],
    );
  }

  /// `User Email`
  String get UserEmail {
    return Intl.message(
      'User Email',
      name: 'UserEmail',
      desc: '',
      args: [],
    );
  }

  /// `Mutual Friends`
  String get MutualFriends1 {
    return Intl.message(
      'Mutual Friends',
      name: 'MutualFriends1',
      desc: '',
      args: [],
    );
  }

  /// `No Mutual Friends`
  String get MutualFriends2 {
    return Intl.message(
      'No Mutual Friends',
      name: 'MutualFriends2',
      desc: '',
      args: [],
    );
  }
}

class AppLocalizationDelegate extends LocalizationsDelegate<S> {
  const AppLocalizationDelegate();

  List<Locale> get supportedLocales {
    return const <Locale>[
      Locale.fromSubtags(languageCode: 'en'),
      Locale.fromSubtags(languageCode: 'hi'),
    ];
  }

  @override
  bool isSupported(Locale locale) => _isSupported(locale);
  @override
  Future<S> load(Locale locale) => S.load(locale);
  @override
  bool shouldReload(AppLocalizationDelegate old) => false;

  bool _isSupported(Locale locale) {
    for (var supportedLocale in supportedLocales) {
      if (supportedLocale.languageCode == locale.languageCode) {
        return true;
      }
    }
    return false;
  }
}
