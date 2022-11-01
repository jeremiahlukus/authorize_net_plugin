package com.jparrack.authorize_net_plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AuthorizeNetPlugin */
public class AuthorizeNetPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, EncryptTransactionCallback {

  private Context mContext;
  private Application mApplication;
  private Intent mIntent;
  private MethodChannel mMethodChannel;
  private Activity activity;
  MethodChannel.Result channelResult = null;

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.mContext = applicationContext;
    mMethodChannel = new MethodChannel(messenger, "authorize_net_plugin");
    mMethodChannel.setMethodCallHandler(this);
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
      channelResult = result;
    } else {
      result.notImplemented();
    }
  }


  public void setupAuthorizeNet(String env, String card_number, String expiration_month, String expiration_year,
                                String card_cvv, String zip_code, String card_holder_name,
                                String api_login_id, String client_id) {
    AcceptSDKApiClient apiClient;
    if (env.equals("production")) {
      apiClient = new AcceptSDKApiClient.Builder(activity,
          AcceptSDKApiClient.Environment.PRODUCTION)
          .connectionTimeout(5000) // optional connection time out in milliseconds
          .build();
    } else {

      apiClient = new AcceptSDKApiClient.Builder(activity,
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

      apiClient.getTokenWithRequest(transactionObject, this);

  }


  @Override
  public void onErrorReceived(ErrorTransactionResponse errorResponse)
  {
    Message error = errorResponse.getFirstErrorMessage();
    if (channelResult != null) {
      channelResult.error("-1", error.getMessageText(), null);
    }
  }

  @Override
  public void onEncryptionFinished(EncryptTransactionResponse response)
  {
    if(channelResult!=null){
      channelResult.success(response.getDataValue());
    }
  }


  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    mMethodChannel.setMethodCallHandler(null);
    mMethodChannel = null;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
    mIntent = binding.getActivity().getIntent();
    mApplication = binding.getActivity().getApplication();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

}