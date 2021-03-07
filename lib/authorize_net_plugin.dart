import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AuthorizeNetPlugin {
  static const MethodChannel _channel =
      const MethodChannel('authorize_net_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> authorizeNet(
      {@required env,
      @required card_number,
      @required expiration_month,
      @required expiration_year,
      @required card_cvv,
      @required zip_code,
      @required card_holder_name,
      @required api_login_id,
      @required client_id}) async {
    final String version =
        await _channel.invokeMethod('authorizeNet', <String, String>{
      'env': env,
      'card_number': card_number,
      'expiration_month': expiration_month,
      'expiration_year': expiration_year,
      'card_cvv': card_cvv,
      'zip_code': zip_code,
      'card_holder_name': card_holder_name,
      'api_login_id': api_login_id,
      'client_id': client_id,
    });
    return version;
  }
}
