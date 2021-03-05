package com.jparrack.authorize_net_plugin_example;

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

import io.flutter.embedding.engine.FlutterEngine;


public class MainActivity extends FlutterActivity implements EncryptTransactionCallback {

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        new MethodChannel(flutterEngine.getDartExecutor(), "authorize_net_plugin").setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("authorizeNet")) {
                        setupAuthorizeNet("370000000000002", "02", "2022",
                                "900", "30028", "Jeremiah",
                                "7594xDmRz", "34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs");

                        result.success("in main");
                    } else {
                        result.notImplemented();
                    }
                }
        );
    }

   public void setupAuthorizeNet(String card_number, String expiration_month, String expiration_year,
                             String card_cvv, String zip_code, String card_holder_name,
                             String api_login_id, String client_id) {
        AcceptSDKApiClient apiClient = new AcceptSDKApiClient.Builder (getActivity(),
                AcceptSDKApiClient.Environment.SANDBOX)
                .connectionTimeout(5000) // optional connection time out in milliseconds
                .build();

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
        Toast.makeText(getActivity(),
                error.getMessageCode() + " :::::::::::: " + error.getMessageText() ,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response)
    {
        Toast.makeText(getActivity(),
                response.getDataDescriptor() + " : " + response.getDataValue(),
                Toast.LENGTH_LONG)
                .show();
    }
}