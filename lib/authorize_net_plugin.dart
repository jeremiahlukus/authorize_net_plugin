import 'dart:async';

import 'package:flutter/services.dart';

class AuthorizeNetPlugin {
  static const MethodChannel _channel =
      const MethodChannel('authorize_net_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> get authorizeNet async {
    final String version =
        await _channel.invokeMethod('authorizeNet', <String, String>{
      'env': 'test',
      'card_number': '370000000000002',
      'expiration_month': '02',
      'expiration_year': '2022',
      'card_cvv': '900',
      'zip_code': '30028',
      'card_holder_name': 'Jeremiah',
      'api_login_id': '7594xDmRz',
      'client_id':
          '34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs',
    });
    return version;
  }
}
