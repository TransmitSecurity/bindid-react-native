# `BindId React Native`
# Introduction

Welcome to the future of customer authentication—strong, portable authentication across all your channels.
## What is BindID?

The BindID service is an app-less, strong portable authenticator offered by Transmit Security. BindID uses FIDO-based biometrics for secure, frictionless, and consistent customer authentication. With one click to create new accounts or sign into existing ones, BindID eliminates passwords and the inconveniences of traditional credential-based logins.<br><br>
[Learn more about how you can boost your experiences with BindID.](https://www.transmitsecurity.com/developer)


BindID is a great solution for various use cases, including:

-   Passwordless authentication for web and mobile applications.
-   User authentication for call centers, IVR, and voice assistance.
-   Frictionless new account opening.
-   Secure verification for infrequent users.

## Authentication and Trust

Combining device-based FIDO biometrics with the OpenID Connect \(OIDC\) protocol, BindID provides a secure and easy-to-deploy authentication process that can be used for any app, in any channel.

Here's how it works:

1.  User performs an action that requires authentication \(such as login\).
2.  User executes an authentication process with BindID—which may also include registering to BindID, registering a strong authenticator \(e.g., FIDO2 biometrics\), and additional data verifications \(e.g., SMS OTP\).
3.  Upon successful authentication, you receive user metadata—which provides user profile info, provides trust indicators, and reflects all their known devices across all providers that use BindID.
4.  If needed, you perform your own additional authentication for the user \(e.g., for new users\), and report this back to BindID, along with a user alias that represents a user in your system.

## Documentation

[BindID Developer Hub](https://developer.bindid.io/docs/guides/introduction/topics/introduction/introduction_chapter_title/index.html)

## Prerequisites

Before you begin, you'll need to have an application configured in the [BindID Admin Portal](https://admin.bindid-sandbox.io/console/#/applications). From the application settings, obtain the client credentials and configure a redirect URI for this client that will receive the authentication result.

- Example CLIENT_ID: `XXXXXXX.XXXXXXXX.dev_6fa9320b.bindid.io"` (This is auto generated in the console)
- Example REDIRECT_URI: `bindidexample://login`
- CUSTOM_SERVER_URL: The BindId server that you work with. (Example: `signin.bindid-sandbox.io`), You can use the Sandbox/Production environment instead.

For more, see [BindID Admin Portal: Get Started](https://developer.bindid.io/docs/guides/admin_portal/topics/getStarted/get_started_admin_portal).


#### Android Setup:
Please follow the [Android Redirection setup](https://developer.bindid.io/docs/guides/quickstart/topics/quickstart_android#step-4-set-up-redirection) to support deeplink in your app.

-----------

## Installation

```sh
# npm
$ npm install --save @transmitsecurity/bindid-react-native

# Yarn
$ yarn add @transmitsecurity/bindid-react-native
```

## Usage

### Initialize the SDK:

```tsx

import XmBindIdSdk from 'bindid-react-native';
import type {  XmBindIdServerEnvironment, XmBindIdConfig } from "bindid-react-native/src/transmit-bind-id-api";

//...

    const serverEnvironment: XmBindIdServerEnvironment = {
      environmentMode: XmBindIdServerEnvironmentMode.Other,
      environmentUrl: "CUSTOM_SERVER_URL"
    };

    /** You can use the sandbox server environment instead. */
    // const serverEnvironment: XmBindIdServerEnvironment = {
    //   environmentMode: XmBindIdServerEnvironmentMode.Sandbox,
    //   environmentUrl: ""
    // };

    const config: XmBindIdConfig = {
      clientId:  "CLIENT_ID",
      serverEnvironment: serverEnvironment
    };

    XmBindIdSdk.initialize(config)
    .then((success: boolean) => {
        console.log(`BindID initialize Completed: ${success}`);
    }).catch((error: XmBindIdError) => {
        console.log(`BindID initialized Failed: ${JSON.stringify(error)}`);
    });
```


### Authenticate API:

```tsx

import XmBindIdSdk from 'bindid-react-native';
import type { XmBindIdAuthenticationRequest , XmBindIdResponse } from "bindid-react-native/src/transmit-bind-id-api";

//...

    const request: XmBindIdAuthenticationRequest = {
        redirectUri: "REDIRECT_URI"
        usePkce: true
    };

    XmBindIdSdk.authenticate(request)
        .then((response: XmBindIdResponse) => {
            console.log(`BindID Authentication Completed: ${JSON.stringify(response)}`);
            //Handle the response by exchange token API to get the id_token, token_access ... 
        }).catch((error: XmBindIdError) => {
            console.log(`BindID Authentication Failed: ${JSON.stringify(error)}`);
    });
```

### Sign Transaction API:

```tsx

import XmBindIdSdk from 'bindid-react-native';
import type { XmBindIdAuthenticationRequest , XmBindIdResponse, XmBindIdTransactionSigningRequest, XmBindIdTransactionSigningDisplayData, XmBindIdTransactionSigningData } from "bindid-react-native/src/transmit-bind-id-api";

//...

    const displayData: XmBindIdTransactionSigningDisplayData = {
        payee: "John Smith",
        paymentAmount: "100$",
        paymentMethod: "PayPal"
    };

    const transactionSigningData: XmBindIdTransactionSigningData = {
        displayData: displayData
    };

    const request: XmBindIdTransactionSigningRequest = {
        redirectUri: "YOUR_REDIRECT_URI",
        transactionSigningData: transactionSigningData,
        encrypted: true,
        usePkce: true
    };

    XmBindIdSdk.signTransaction(request)
        .then((response: XmBindIdResponse) => {
            console.log(`BindID Sign Transaction Completed: ${JSON.stringify(response)}`);
            //Handle the response by exchange token API to get the id_token, token_access ...
        }).catch((error: XmBindIdError) => {
            console.log(`BindID Sign Transaction Failed: ${JSON.stringify(error)}`);
     });
```


### Exchange Token API:

```tsx

import XmBindIdSdk from 'bindid-react-native';
import type { XmBindIdExchangeTokenResponse } from "bindid-react-native/src/transmit-bind-id-api";

//...

     XmBindIdSdk.exchangeToken("THE_RESPONSE_FROM_AUTHENTICATE_API/SIGN_TRANSACTION_API")
        .then((response: XmBindIdExchangeTokenResponse) => {
            console.log(`BindID Exchange Token Completed: ${JSON.stringify(response)}`);
            //Handle the id_token by validate API
        }).catch((error: XmBindIdError) => {
            console.log(`BindID Exchange Token Failed: ${error.message}`);
    });
```

## Author

Transmit Security, https://github.com/TransmitSecurity

## License

This project is licensed under the MIT license. See the LICENSE file for more info.

