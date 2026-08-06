# authorize_net_plugin

A simple wrapper around the AuthorizeNet android and ios sdk 

## Setting up 

Android needs a bit of setup. The plugin already declares the AuthorizeNet SDK
dependency for you, but the SDK is published on jitpack, so your app has to add
that repository. In `android/build.gradle`:

```
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = "https://jitpack.io" }
    }
}
```

The SDK still depends on the old Android support library, so Jetifier has to be
on. In `android/gradle.properties`:

```
android.useAndroidX=true
android.enableJetifier=true
```

Then go into your android/app/src/main/AndroidManifest.xml file and add 
xmlns:tools="http://schemas.android.com/tools" in the <manifest> tag.
Then in your <application> tag add tools:replace="android:label"

The plugin requires `minSdk` 26 or higher.


## How to use

 - add to pubfile
 

Get the Card Token used to make the createTransactionRequest request

```
authorizeNetToken = await AuthorizeNetPlugin.authorizeNetToken(
          env: 'test',
          cardNumber: '370000000000002',
          expirationMonth: '02',
          expirationYear: '2022',
          cardCvv: '900',
          zipCode: '30028',
          cardHolderName: 'Jeremiah',
          apiLoginId: '7594xDmRz',
          clientId:
              '34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs');
```

### Required and optional fields

`cardCvv`, `zipCode` and `cardHolderName` are optional in the underlying
Android and iOS SDKs, so they are optional here too. Omit the ones you don't
collect (an empty string is treated the same as omitting it):

```
authorizeNetToken = await AuthorizeNetPlugin.authorizeNetToken(
          env: 'test',
          cardNumber: '370000000000002',
          expirationMonth: '02',
          expirationYear: '2028',
          apiLoginId: '7594xDmRz',
          clientId:
              '34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs');
```

| Field | Required | Notes |
| --- | --- | --- |
| `env` | yes | `production` for live, anything else for sandbox |
| `cardNumber` | yes | |
| `expirationMonth` | yes | `MM` |
| `expirationYear` | yes | `YY` or `YYYY` |
| `apiLoginId` | yes | |
| `clientId` | yes | Public client key |
| `cardCvv` | no | Validated by the SDK when supplied |
| `zipCode` | no | Validated by the SDK when supplied |
| `cardHolderName` | no | Validated by the SDK when supplied |

 
This will spit out the token you use to make the createTransactionRequest POST request 


```
   {
    "createTransactionRequest": {
        "merchantAuthentication": {
            "name": "YOUR_API_LOGIN_ID",
            "transactionKey": "YOUR_TRANSACTION_KEY"
        },
        "refId": "123456",
        "transactionRequest": {
            "transactionType": "authCaptureTransaction",
            "amount": "5",
            "payment": {
                "opaqueData": {
                    "dataDescriptor": "COMMON.ACCEPT.INAPP.PAYMENT",
                    "dataValue": authorizeNetToken
                }
            }
        }
    }
}
```