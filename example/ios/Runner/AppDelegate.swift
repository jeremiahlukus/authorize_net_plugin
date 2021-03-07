import UIKit
import Flutter
import AuthorizeNetAccept
@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    
    let controller: FlutterViewController = window?.rootViewController as! FlutterViewController
    
    let channel = FlutterMethodChannel(name: "authorize_net_plugin", binaryMessenger: controller as! FlutterBinaryMessenger)
    
    channel.setMethodCallHandler{(methodCall, result) in
        if(methodCall.method == "authorizeNet"){
            let handler = AcceptSDKHandler(environment: AcceptSDKEnvironment.ENV_TEST)
            let request = AcceptSDKRequest()
                 request.merchantAuthentication.name = "7594xDmRz"
                 request.merchantAuthentication.clientKey = "34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs"
                 request.securePaymentContainerRequest.webCheckOutDataType.token.cardNumber = "370000000000002"
                 request.securePaymentContainerRequest.webCheckOutDataType.token.expirationMonth = "02"
                 request.securePaymentContainerRequest.webCheckOutDataType.token.expirationYear = "2022"
                 request.securePaymentContainerRequest.webCheckOutDataType.token.cardCode = "900"
            
            handler!.getTokenWithRequest(request, successHandler: { (inResponse:AcceptSDKTokenResponse) -> () in
                DispatchQueue.main.async {
                    print("Token--->%@", inResponse.getOpaqueData().getDataValue())
                    var output = String(format: "Response: %@\nData Value: %@ \nDescription: %@", inResponse.getMessages().getResultCode(), inResponse.getOpaqueData().getDataValue(), inResponse.getOpaqueData().getDataDescriptor())
                    output = output + String(format: "\nMessage Code: %@\nMessage Text: %@", inResponse.getMessages().getMessages()[0].getCode(), inResponse.getMessages().getMessages()[0].getText())
                    result(output)

                };
            }) { (inError:AcceptSDKErrorResponse) -> () in
              
                let output = String(format: "Response:  %@\nError code: %@\nError text:   %@", inError.getMessages().getResultCode(), inError.getMessages().getMessages()[0].getCode(), inError.getMessages().getMessages()[0].getText())
                print(output)
                result(output)
            }
        }
    
    }
    
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
}


