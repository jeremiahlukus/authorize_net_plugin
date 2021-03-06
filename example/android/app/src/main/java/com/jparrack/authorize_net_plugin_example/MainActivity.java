package com.jparrack.authorize_net_plugin_example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import io.flutter.embedding.engine.FlutterEngine;


public class MainActivity extends FlutterActivity implements EncryptTransactionCallback {

    MethodChannel mainMethodChannel;
    MethodChannel.Result channelResult =null;
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        mainMethodChannel =  new MethodChannel(flutterEngine.getDartExecutor(), "authorize_net_plugin");
        mainMethodChannel.setMethodCallHandler(
                (call, result) -> {
                    channelResult = result;
                    if (call.method.equals("authorizeNet")) {
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
        );
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

        apiClient.getTokenWithRequest(transactionObject, this);
    }


    @Override
    public void onErrorReceived(ErrorTransactionResponse errorResponse)
    {
        Message error = errorResponse.getFirstErrorMessage();
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("It worked").setMessage(
                error.getMessageCode() + " :: " + error.getMessageText()
        ).setPositiveButton("Yes", (dialogInterface, i) -> {
            //set what would happen when positive button is clicked
            finish();
        }).show();
    }


    WeakReference<String> responseRef = null;
  // how to pass  response.getDataValue() up ?
    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response)
    {
        if(channelResult!=null){
            channelResult.success(response.getDataValue());
        }
    }


}