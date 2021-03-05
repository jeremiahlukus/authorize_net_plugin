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
    final String version = await _channel.invokeMethod('authorizeNet');
    return version;
  }
}
