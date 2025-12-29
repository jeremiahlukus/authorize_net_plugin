import 'package:authorize_net_plugin/authorize_net_plugin.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const MethodChannel channel = MethodChannel('authorize_net_plugin');

  // Helper to set up mock method call handler
  void setMockHandler(Future<dynamic> Function(MethodCall call)? handler) {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, handler);
  }

  group('AuthorizeNetPlugin', () {
    tearDown(() {
      setMockHandler(null);
    });

    group('authorizeNetToken', () {
      test('returns token on successful call', () async {
        const expectedToken = 'test_token_12345';

        setMockHandler((MethodCall methodCall) async {
          expect(methodCall.method, 'authorizeNetToken');
          return expectedToken;
        });

        final result = await AuthorizeNetPlugin.authorizeNetToken(
          env: 'sandbox',
          cardNumber: '4111111111111111',
          expirationMonth: '12',
          expirationYear: '2025',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: 'John Doe',
          apiLoginId: 'test_api_login',
          clientId: 'test_client_id',
        );

        expect(result, expectedToken);
      });

      test('passes correct parameters to native channel', () async {
        Map<String, dynamic>? capturedArguments;

        setMockHandler((MethodCall methodCall) async {
          capturedArguments = Map<String, dynamic>.from(methodCall.arguments);
          return 'token';
        });

        await AuthorizeNetPlugin.authorizeNetToken(
          env: 'production',
          cardNumber: '5555555555554444',
          expirationMonth: '06',
          expirationYear: '2026',
          cardCvv: '456',
          zipCode: '67890',
          cardHolderName: 'Jane Smith',
          apiLoginId: 'prod_api_login',
          clientId: 'prod_client_id',
        );

        expect(capturedArguments, isNotNull);
        expect(capturedArguments!['env'], 'production');
        expect(capturedArguments!['card_number'], '5555555555554444');
        expect(capturedArguments!['expiration_month'], '06');
        expect(capturedArguments!['expiration_year'], '2026');
        expect(capturedArguments!['card_cvv'], '456');
        expect(capturedArguments!['zip_code'], '67890');
        expect(capturedArguments!['card_holder_name'], 'Jane Smith');
        expect(capturedArguments!['api_login_id'], 'prod_api_login');
        expect(capturedArguments!['client_id'], 'prod_client_id');
      });

      test('works with sandbox environment', () async {
        String? capturedEnv;

        setMockHandler((MethodCall methodCall) async {
          capturedEnv = (methodCall.arguments as Map)['env'];
          return 'sandbox_token';
        });

        final result = await AuthorizeNetPlugin.authorizeNetToken(
          env: 'sandbox',
          cardNumber: '4111111111111111',
          expirationMonth: '12',
          expirationYear: '2025',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: 'Test User',
          apiLoginId: 'sandbox_api',
          clientId: 'sandbox_client',
        );

        expect(capturedEnv, 'sandbox');
        expect(result, 'sandbox_token');
      });

      test('works with production environment', () async {
        String? capturedEnv;

        setMockHandler((MethodCall methodCall) async {
          capturedEnv = (methodCall.arguments as Map)['env'];
          return 'production_token';
        });

        final result = await AuthorizeNetPlugin.authorizeNetToken(
          env: 'production',
          cardNumber: '4111111111111111',
          expirationMonth: '12',
          expirationYear: '2025',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: 'Test User',
          apiLoginId: 'prod_api',
          clientId: 'prod_client',
        );

        expect(capturedEnv, 'production');
        expect(result, 'production_token');
      });

      test('throws PlatformException on native error', () async {
        setMockHandler((MethodCall methodCall) async {
          throw PlatformException(
            code: 'INVALID_CARD',
            message: 'The card number is invalid',
            details: 'Card validation failed',
          );
        });

        expect(
          () => AuthorizeNetPlugin.authorizeNetToken(
            env: 'sandbox',
            cardNumber: 'invalid_card',
            expirationMonth: '12',
            expirationYear: '2025',
            cardCvv: '123',
            zipCode: '12345',
            cardHolderName: 'Test User',
            apiLoginId: 'test_api',
            clientId: 'test_client',
          ),
          throwsA(isA<PlatformException>()),
        );
      });

      test('throws PlatformException with correct error code', () async {
        setMockHandler((MethodCall methodCall) async {
          throw PlatformException(
            code: 'AUTHENTICATION_ERROR',
            message: 'Invalid API credentials',
          );
        });

        try {
          await AuthorizeNetPlugin.authorizeNetToken(
            env: 'sandbox',
            cardNumber: '4111111111111111',
            expirationMonth: '12',
            expirationYear: '2025',
            cardCvv: '123',
            zipCode: '12345',
            cardHolderName: 'Test User',
            apiLoginId: 'invalid_api',
            clientId: 'invalid_client',
          );
          fail('Expected PlatformException');
        } on PlatformException catch (e) {
          expect(e.code, 'AUTHENTICATION_ERROR');
          expect(e.message, 'Invalid API credentials');
        }
      });

      test('handles different card types', () async {
        final cardNumbers = [
          '4111111111111111', // Visa
          '5555555555554444', // Mastercard
          '378282246310005', // American Express
          '6011111111111117', // Discover
        ];

        for (final cardNumber in cardNumbers) {
          String? capturedCardNumber;

          setMockHandler((MethodCall methodCall) async {
            capturedCardNumber = (methodCall.arguments as Map)['card_number'];
            return 'token_$cardNumber';
          });

          final result = await AuthorizeNetPlugin.authorizeNetToken(
            env: 'sandbox',
            cardNumber: cardNumber,
            expirationMonth: '12',
            expirationYear: '2025',
            cardCvv: '123',
            zipCode: '12345',
            cardHolderName: 'Test User',
            apiLoginId: 'test_api',
            clientId: 'test_client',
          );

          expect(capturedCardNumber, cardNumber);
          expect(result, 'token_$cardNumber');
        }
      });

      test('handles expiration dates correctly', () async {
        Map<String, dynamic>? capturedArguments;

        setMockHandler((MethodCall methodCall) async {
          capturedArguments = Map<String, dynamic>.from(methodCall.arguments);
          return 'token';
        });

        await AuthorizeNetPlugin.authorizeNetToken(
          env: 'sandbox',
          cardNumber: '4111111111111111',
          expirationMonth: '01',
          expirationYear: '2030',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: 'Test User',
          apiLoginId: 'test_api',
          clientId: 'test_client',
        );

        expect(capturedArguments!['expiration_month'], '01');
        expect(capturedArguments!['expiration_year'], '2030');
      });

      test('handles special characters in cardholder name', () async {
        String? capturedName;

        setMockHandler((MethodCall methodCall) async {
          capturedName = (methodCall.arguments as Map)['card_holder_name'];
          return 'token';
        });

        await AuthorizeNetPlugin.authorizeNetToken(
          env: 'sandbox',
          cardNumber: '4111111111111111',
          expirationMonth: '12',
          expirationYear: '2025',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: "John O'Brien-Smith",
          apiLoginId: 'test_api',
          clientId: 'test_client',
        );

        expect(capturedName, "John O'Brien-Smith");
      });

      test('invokes correct method name', () async {
        String? methodName;

        setMockHandler((MethodCall methodCall) async {
          methodName = methodCall.method;
          return 'token';
        });

        await AuthorizeNetPlugin.authorizeNetToken(
          env: 'sandbox',
          cardNumber: '4111111111111111',
          expirationMonth: '12',
          expirationYear: '2025',
          cardCvv: '123',
          zipCode: '12345',
          cardHolderName: 'Test User',
          apiLoginId: 'test_api',
          clientId: 'test_client',
        );

        expect(methodName, 'authorizeNetToken');
      });
    });
  });
}
