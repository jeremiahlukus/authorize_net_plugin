# authorize_net_plugin

A simple wrapper around the AuthorizeNet android and ios sdk 

## How to use

 - add to pubfile
 

After you take the apple or google payment get the card token by passing in 

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