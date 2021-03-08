package com.jparrack.authorize_net_plugin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import net.authorize.acceptsdk.AcceptSDKApiClient;
import net.authorize.acceptsdk.datamodel.common.Message;
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication;
import net.authorize.acceptsdk.datamodel.transaction.CardData;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionType;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

import java.lang.ref.WeakReference;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AuthorizeNetPlugin */
public class AuthorizeNetPlugin extends FlutterActivity implements FlutterPlugin, MethodCallHandler, EncryptTransactionCallback {

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  MethodChannel.Result channelResult =null;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    System.out.println("::::::::::::::::::::::::");
    System.out.println("Im attaching");
    System.out.println("::::::::::::::::::::::::");
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "authorize_net_plugin");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("authorizeNetToken")) {
      final String env = call.argument("env"),
              card_number = call.argument("card_number"),
              expiration_month = call.argument("expiration_month"),
              expiration_year = call.argument("expiration_year"),
              card_cvv = call.argument("card_cvv"),
              zip_code = call.argument("zip_code"),
              card_holder_name = call.argument("card_holder_name"),
              api_login_id = call.argument("api_login_id"),
              client_id = call.argument("client_id");

      setupAuthorizeNet(env, card_number, expiration_month, expiration_year,
              card_cvv, zip_code, card_holder_name,
              api_login_id, client_id);
    } else {
      result.notImplemented();
    }
  }

  public void setupAuthorizeNet(String env, String card_number, String expiration_month, String expiration_year,
                                String card_cvv, String zip_code, String card_holder_name,
                                String api_login_id, String client_id) {
    AcceptSDKApiClient apiClient;
    if (env == "production") {
      apiClient = new AcceptSDKApiClient.Builder(getActivity(),
              AcceptSDKApiClient.Environment.PRODUCTION)
              .connectionTimeout(5000) // optional connection time out in milliseconds
              .build();
    } else {
      apiClient = new AcceptSDKApiClient.Builder(getActivity(),
              AcceptSDKApiClient.Environment.SANDBOX)
              .connectionTimeout(5000) // optional connection time out in milliseconds
              .build();
    }

    CardData cardData = new CardData.Builder(card_number,
            expiration_month, // MM
            expiration_year) // YYYY
            .cvvCode(card_cvv) // Optional
            .zipCode(zip_code)// Optional
            .cardHolderName(card_holder_name)// Optional
            .build();

    ClientKeyBasedMerchantAuthentication merchantAuthentication = ClientKeyBasedMerchantAuthentication.
            createMerchantAuthentication(api_login_id, client_id);
    EncryptTransactionObject transactionObject = TransactionObject.
            createTransactionObject(TransactionType.SDK_TRANSACTION_ENCRYPTION)// type of transaction object
            .cardData(cardData) // card data to be encrypted
            .merchantAuthentication(merchantAuthentication) //Merchant authentication
            .build();

    System.out.println("::::::::::::::::::::::::");
    System.out.println(this);
    System.out.println("::::::::::::::::::::::::");

    apiClient.getTokenWithRequest(transactionObject, this);
  }

  @Override
  public void onErrorReceived(ErrorTransactionResponse errorResponse)
  {
    Message error = errorResponse.getFirstErrorMessage();
    System.out.println("::::::::::::::::::::::::");
    System.out.println( error.getMessageText());
    System.out.println("::::::::::::::::::::::::");
    Toast.makeText(getActivity(),
            error.getMessageCode() + " : " + error.getMessageText() ,
            Toast.LENGTH_LONG)
            .show();
  }


  WeakReference<String> responseRef = null;
  // how to pass  response.getDataValue() up ?
  @Override
  public void onEncryptionFinished(EncryptTransactionResponse response)
  {
    if(channelResult!=null){
      System.out.println("::::::::::::::::::::::::");
      System.out.println(response.getDataValue());
      System.out.println("::::::::::::::::::::::::");
      channelResult.success(response.getDataValue());
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    System.out.println("::::::::::::::::::::::::");
    System.out.println("Im detaching");
    System.out.println("::::::::::::::::::::::::");
    channel.setMethodCallHandler(null);
  }

}