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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("It worked").setMessage(
                error.getMessageCode() + " :: " + error.getMessageText()
        ).setPositiveButton("Yes", (dialogInterface, i) -> {
            //set what would happen when positive button is clicked
            finish();
        }).show();
    }

    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response)
    {
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    byte[] requestBody = getBody(response.getDataValue());
                    URL url = new URL("https://apitest.authorize.net/xml/v1/request.api");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");

                    OutputStream os = con.getOutputStream();
                    os.write(requestBody);
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    String body = con.getResponseMessage();
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println(requestBody);
                    System.out.println(responseCode);
                    System.out.println(body);
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println(e);
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                }
            }
        });
        thread.start();

    }

    public byte[] getBody(String dataValue) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("createTransactionRequest", createTransactionRequest(dataValue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString().getBytes();
    }

    private JSONObject createTransactionRequest(String dataValue) throws JSONException {
        JSONObject params = new JSONObject();
        params.put( "merchantAuthentication", merchantAuthentication());
        params.put( "transactionRequest", transactionRequest(dataValue));
        return params;
    }

    private JSONObject merchantAuthentication() throws JSONException {
        JSONObject params = new JSONObject();
        params.put( "name", "7594xDmRz");
        params.put( "transactionKey", "79j2R59js2jUThR2");

        return params;
    }


    private JSONObject transactionRequest(String dataValue) throws JSONException {
        JSONObject params = new JSONObject();
        params.put( "transactionType", "authCaptureTransaction");
        params.put( "amount", "5");
        params.put( "payment", payment(dataValue));
        return params;
    }

    private JSONObject payment(String dataValue) throws JSONException {
        JSONObject params = new JSONObject();
        params.put( "opaqueData", opaqueData(dataValue));
        return params;
    }
    private JSONObject opaqueData(String dataValue) throws JSONException {
        JSONObject params = new JSONObject();
        params.put( "dataDescriptor", "COMMON.ACCEPT.INAPP.PAYMENT");
        params.put( "dataValue", dataValue);
        return params;
    }

}