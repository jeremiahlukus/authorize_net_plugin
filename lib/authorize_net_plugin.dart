import 'dart:async';

import 'package:flutter/services.dart';

class AuthorizeNetPlugin {
  static const MethodChannel _channel =
      const MethodChannel('authorize_net_plugin');

  static Future<String> authorizeNetToken(
      {required String env,
      required String cardNumber,
      required String expirationMonth,
      required String expirationYear,
      required String cardCvv,
      String? zipCode,
      String? cardHolderName,
      required String apiLoginId,
      required String clientId}) async {
    final _arguments = <String, String>{
      'env': env,
      'card_number': cardNumber,
      'expiration_month': expirationMonth,
      'expiration_year': expirationYear,
      'card_cvv': cardCvv,
      'api_login_id': apiLoginId,
      'client_id': clientId,
    };
    if (zipCode != null) {
      _arguments['zip_code'] = zipCode;
    }
    if (cardHolderName != null) {
      _arguments['card_holder_name'] = cardHolderName;
    }
    final String version =
        await _channel.invokeMethod('authorizeNetToken', _arguments);
    return version;
  }
}