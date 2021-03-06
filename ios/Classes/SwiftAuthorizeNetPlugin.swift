import Flutter
import UIKit
import AuthorizeNetAccept

public class SwiftAuthorizeNetPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "authorize_net_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftAuthorizeNetPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if(call.method == "authorizeNetToken"){
        let argErr = FlutterError(code: "BAD_ARGS", message: "Failed to parse arguments!", details: nil)
        guard let args = call.arguments as? Dictionary<String, String> else {result(argErr); return }
        guard let env = args["env"] else { result(argErr); return }
        guard let card_number = args["card_number"] else { result(argErr); return }
        guard let expiration_month = args["expiration_month"] else { result(argErr); return }
        guard let expiration_year = args["expiration_year"] else { result(argErr); return }
        guard let card_cvv = args["card_cvv"] else { result(argErr); return }
        guard let zip_code = args["zip_code"] else { result(argErr); return }
        guard let card_holder_name = args["card_holder_name"] else { result(argErr); return }
        guard let api_login_id = args["api_login_id"] else { result(argErr); return }
        guard let client_id = args["client_id"] else { result(argErr); return }
        
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
