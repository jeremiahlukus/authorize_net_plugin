import Flutter
import UIKit
import AuthorizeNetAccept

public class SwiftAuthorizeNetPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "authorize_net_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftAuthorizeNetPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  /// Returns nil when the argument is missing, null or blank so the SDK skips
  /// validating it instead of rejecting an empty value.
  private func optionalArg(_ args: Dictionary<String, Any>, _ key: String) -> String? {
    guard let value = args[key] as? String, !value.isEmpty else { return nil }
    return value
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if(call.method == "authorizeNetToken"){
        let argErr = FlutterError(code: "BAD_ARGS", message: "Failed to parse arguments!", details: nil)
        guard let args = call.arguments as? Dictionary<String, Any> else {result(argErr); return }
        guard let env = args["env"] as? String else { result(argErr); return }
        guard let card_number = args["card_number"] as? String else { result(argErr); return }
        guard let expiration_month = args["expiration_month"] as? String else { result(argErr); return }
        guard let expiration_year = args["expiration_year"] as? String else { result(argErr); return }
        guard let api_login_id = args["api_login_id"] as? String else { result(argErr); return }
        guard let client_id = args["client_id"] as? String else { result(argErr); return }

        // Optional in the Accept SDK: only sent when present, since the SDK
        // validates any value it is given (an empty string would be rejected).
        let card_cvv = optionalArg(args, "card_cvv")
        let zip_code = optionalArg(args, "zip_code")
        let card_holder_name = optionalArg(args, "card_holder_name")

        var handler = AcceptSDKHandler(environment: AcceptSDKEnvironment.ENV_TEST)
        if(env == "production"){
            handler = AcceptSDKHandler(environment: AcceptSDKEnvironment.ENV_LIVE)
        }

        let request = AcceptSDKRequest()
             request.merchantAuthentication.name = api_login_id
             request.merchantAuthentication.clientKey = client_id
             request.securePaymentContainerRequest.webCheckOutDataType.token.cardNumber = card_number
             request.securePaymentContainerRequest.webCheckOutDataType.token.expirationMonth = expiration_month
             request.securePaymentContainerRequest.webCheckOutDataType.token.expirationYear = expiration_year
             request.securePaymentContainerRequest.webCheckOutDataType.token.cardCode = card_cvv
        request.securePaymentContainerRequest.webCheckOutDataType.token.fullName = card_holder_name
        request.securePaymentContainerRequest.webCheckOutDataType.token.zip = zip_code

        handler!.getTokenWithRequest(request, successHandler: { (inResponse:AcceptSDKTokenResponse) -> () in
            DispatchQueue.main.async {
                result(inResponse.getOpaqueData().getDataValue())
            };
        }) { (inError:AcceptSDKErrorResponse) -> () in
            result(inError.getMessages().getMessages()[0].getText())
        }
    }
    }
  }
