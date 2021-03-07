import 'dart:async';

import 'package:authorize_net_plugin/authorize_net_plugin.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _authorizeNet = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String authorizeNet;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      authorizeNet = await AuthorizeNetPlugin.authorizeNet(
          env: 'test',
          card_number: '370000000000002',
          expiration_month: '02',
          expiration_year: '2022',
          card_cvv: '900',
          zip_code: '30028',
          card_holder_name: 'Jeremiah',
          api_login_id: '7594xDmRz',
          client_id:
              '34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs');
    } on PlatformException {
      authorizeNet = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _authorizeNet = authorizeNet;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_authorizeNet\n'),
        ),
      ),
    );
  }
}
