import 'package:authorize_net_plugin/authorize_net_plugin.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const MethodChannel channel = MethodChannel('authorize_net_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return 'someToken';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(
        await AuthorizeNetPlugin.authorizeNetToken(
            env: "env",
            cardNumber: "card_number",
            expirationMonth: "expiration_month",
            expirationYear: "expiration_year",
            cardCvv: "card_cvv",
            zipCode: "zip_code",
            cardHolderName: "card_holder_name",
            apiLoginId: "api_login_id",
            clientId: "client_id"),
        'someToken');
  });
}
