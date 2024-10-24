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
        
        // Cast arguments to [String: Any] to handle nullable values
        guard let args = call.arguments as? [String: Any?] else {
            result(argErr)
            return
        }
        
        // Check required parameters with proper type casting
        guard let env = args["env"] as? String,
              let card_number = args["card_number"] as? String,
              let expiration_month = args["expiration_month"] as? String,
              let expiration_year = args["expiration_year"] as? String,
              let card_cvv = args["card_cvv"] as? String,
              let api_login_id = args["api_login_id"] as? String,
              let client_id = args["client_id"] as? String
        else {
            result(argErr)
            return
        }
        
        var handler = AcceptSDKHandler(environment: AcceptSDKEnvironment.ENV_TEST)
        if(env == "production"){
            handler = AcceptSDKHandler(environment: AcceptSDKEnvironment.ENV_LIVE)
        }

        let request = AcceptSDKRequest()
        request.merchantAuthentication.name = api_login_id
        request.merchantAuthentication.clientKey = client_id
        
        let token = request.securePaymentContainerRequest.webCheckOutDataType.token
        token.cardNumber = card_number
        token.expirationMonth = expiration_month
        token.expirationYear = expiration_year
        token.cardCode = card_cvv
        
        // Handle optional zip code
        if let zip_code = args["zip_code"] as? String {
            if !zip_code.isEmpty {
                token.zip = zip_code
            }
        }
        
        // Handle optional card holder name
        if let card_holder_name = args["card_holder_name"] as? String {
            if !card_holder_name.isEmpty {
                token.fullName = card_holder_name
            }
        }
        
        handler!.getTokenWithRequest(request, successHandler: { (inResponse:AcceptSDKTokenResponse) -> () in
            DispatchQueue.main.async {
                result(inResponse.getOpaqueData().getDataValue())
            }
        }) { (inError:AcceptSDKErrorResponse) -> () in
            DispatchQueue.main.async {
                result(FlutterError(code: "AUTH_ERROR",
                                  message: inError.getMessages().getMessages()[0].getText(),
                                  details: nil))
            }
        }
    } else {
        result(FlutterMethodNotImplemented)
    }
  }
}