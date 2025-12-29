package com.jparrack.authorize_net_plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

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

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/** AuthorizeNetPlugin */
public class AuthorizeNetPlugin implements
        FlutterPlugin,
        ActivityAware,
        MethodChannel.MethodCallHandler,
        EncryptTransactionCallback {

  private Context mContext;
  private Application mApplication;
  private Intent mIntent;
  private MethodChannel mMethodChannel;
  private Activity activity;

  private Result channelResult = null;

  private void setupChannel(Context applicationContext, BinaryMessenger messenger) {
    this.mContext = applicationContext;
    mMethodChannel = new MethodChannel(messenger, "authorize_net_plugin");
    mMethodChannel.setMethodCallHandler(this);
  }

  // -------- FlutterPlugin lifecycle ----------

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    setupChannel(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (mMethodChannel != null) {
      mMethodChannel.setMethodCallHandler(null);
      mMethodChannel = null;
    }
  }

  // -------- ActivityAware lifecycle ----------

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    mIntent = binding.getActivity().getIntent();
    mApplication = binding.getActivity().getApplication();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  // -------- Method Call Handler ----------

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    if (call.method.equals("authorizeNetToken")) {

      final String env = call.argument("env");
      final String card_number = call.argument("card_number");
      final String expiration_month = call.argument("expiration_month");
      final String expiration_year = call.argument("expiration_year");
      final String card_cvv = call.argument("card_cvv");
      final String zip_code = call.argument("zip_code");
      final String card_holder_name = call.argument("card_holder_name");
      final String api_login_id = call.argument("api_login_id");
      final String client_id = call.argument("client_id");

      channelResult = result;

      setupAuthorizeNet(
              env,
              card_number,
              expiration_month,
              expiration_year,
              card_cvv,
              zip_code,
              card_holder_name,
              api_login_id,
              client_id
      );

    } else {
      result.notImplemented();
    }
  }

  // -------- Authorize.Net SDK logic ----------

  public void setupAuthorizeNet(
          String env,
          String card_number,
          String expiration_month,
          String expiration_year,
          String card_cvv,
          String zip_code,
          String card_holder_name,
          String api_login_id,
          String client_id
  ) {

    AcceptSDKApiClient apiClient;

    if ("production".equals(env)) {
      apiClient = new AcceptSDKApiClient
              .Builder(activity, AcceptSDKApiClient.Environment.PRODUCTION)
              .connectionTimeout(5000)
              .build();
    } else {
      apiClient = new AcceptSDKApiClient
              .Builder(activity, AcceptSDKApiClient.Environment.SANDBOX)
              .connectionTimeout(5000)
              .build();
    }

    CardData cardData = new CardData
            .Builder(card_number, expiration_month, expiration_year)
            .cvvCode(card_cvv)
            .zipCode(zip_code)
            .cardHolderName(card_holder_name)
            .build();

    ClientKeyBasedMerchantAuthentication merchantAuthentication =
            ClientKeyBasedMerchantAuthentication.createMerchantAuthentication(
                    api_login_id,
                    client_id
            );

    EncryptTransactionObject transactionObject =
            TransactionObject
                    .createTransactionObject(TransactionType.SDK_TRANSACTION_ENCRYPTION)
                    .cardData(cardData)
                    .merchantAuthentication(merchantAuthentication)
                    .build();

    apiClient.getTokenWithRequest(transactionObject, this);
  }

  // -------- Authorize.Net Callbacks ----------

  @Override
  public void onErrorReceived(ErrorTransactionResponse errorResponse) {
    Message error = errorResponse.getFirstErrorMessage();
    if (channelResult != null) {
      channelResult.error("-1", error.getMessageText(), null);
    }
  }

  @Override
  public void onEncryptionFinished(EncryptTransactionResponse response) {
    if (channelResult != null) {
      channelResult.success(response.getDataValue());
    }
  }
}
