import 'dart:async';

import 'package:flutter/services.dart';

class AuthorizeNetPlugin {
  static const MethodChannel _channel = const MethodChannel('authorize_net_plugin');

  /// Requests a payment nonce (opaque data value) for the given card.
  ///
  /// [env] is either `production` or anything else for the sandbox.
  /// [cardNumber], [expirationMonth] (MM) and [expirationYear] (YY or YYYY) are
  /// required by both the Android and iOS AuthorizeNet SDKs.
  ///
  /// [cardCvv], [zipCode] and [cardHolderName] are optional in both SDKs. Leave
  /// them `null` (or empty) to omit them from the request. When supplied they
  /// are validated by the SDK, so an empty string is treated the same as `null`
  /// rather than being sent as a blank value.
  static Future<String> authorizeNetToken({
    required String env,
    required String cardNumber,
    required String expirationMonth,
    required String expirationYear,
    required String apiLoginId,
    required String clientId,
    String? cardCvv,
    String? zipCode,
    String? cardHolderName,
  }) async {
    final Map<String, String> arguments = <String, String>{
      'env': env,
      'card_number': cardNumber,
      'expiration_month': expirationMonth,
      'expiration_year': expirationYear,
      'api_login_id': apiLoginId,
      'client_id': clientId,
    };

    if (cardCvv != null && cardCvv.isNotEmpty) arguments['card_cvv'] = cardCvv;
    if (zipCode != null && zipCode.isNotEmpty) arguments['zip_code'] = zipCode;
    if (cardHolderName != null && cardHolderName.isNotEmpty) arguments['card_holder_name'] = cardHolderName;

    final String version = await _channel.invokeMethod('authorizeNetToken', arguments);
    return version;
  }
}
