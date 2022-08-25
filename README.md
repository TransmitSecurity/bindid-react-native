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
Please follow The [Android Integration setup](https://developer.bindid.io/docs/guides/quickstart/topics/quickstart_android#step-2-add-sdk-to-your-project) and [Android Redirection setup](https://developer.bindid.io/docs/guides/quickstart/topics/quickstart_android#step-4-set-up-redirection) to support deeplink in your app.

-----------

## Installation

### [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#authenticating-to-github-packages)

You need an access token to install packages, You can use a personal access token (PAT) to authenticate to GitHub Packages or the GitHub API.

1. Create a GitHub personal access token [PAT](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

2. Authenticate by adding your personal access token to your ~/.npmrc file, edit the ~/.npmrc file for your project to include the following line, replacing TOKEN with your personal access token. Create a new ~/.npmrc file if one doesn't exist.

    ```
    //npm.pkg.github.com/:_authToken=TOKEN
    ```

3. Add to the global `~/.npmrc` or the project level `.npmrc` file the following line to route to Transmit Security packages on Github for installation.

    ```
    @transmitsecurity:registry=https://npm.pkg.github.com
    ```

Install the bindid-react-native package

```sh
# npm
$ npm install --save @transmitsecurity/bindid-react-native

# Yarn
$ yarn add @transmitsecurity/bindid-react-native
```

### Manually

Download the BindId react native package `bindid-react-native-${version}-npm.tgz` from the [packages page](https://github.com/TransmitSecurity/bindid-react-native/packages) and install the bindid-react-native package from the local path

```sh
# npm
$ npm install --save /local_path_to_the_package/bindid-react-native-${version}-npm.tgz

# Yarn
$ yarn add /local_path_to_the_package/bindid-react-native-${version}-npm.tgz
```

## Usage

### Initialize the SDK:

```tsx

import XmBindIdSdk from '@transmitsecurity/bindid-react-native';
import type {  XmBindIdServerEnvironment, XmBindIdConfig } from "@transmitsecurity/bindid-react-native/src/transmit-bind-id-api";

//...

    const serverEnvironment: XmBindIdServerEnvironment = {
      environmentMode: XmBindIdServerEnvironmentMode.Other,
      environmentUrl: "CUSTOM_SERVER_URL"
    };

    /** You can use the sandbox/production server environment instead. */
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

import XmBindIdSdk from '@transmitsecurity/bindid-react-native';
import type { XmBindIdAuthenticationRequest , XmBindIdResponse } from "@transmitsecurity/bindid-react-native/src/transmit-bind-id-api";

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

import XmBindIdSdk from '@transmitsecurity/bindid-react-native';
import type { XmBindIdAuthenticationRequest , XmBindIdResponse, XmBindIdTransactionSigningRequest, XmBindIdTransactionSigningDisplayData, XmBindIdTransactionSigningData } from "@transmitsecurity/bindid-react-native/src/transmit-bind-id-api";

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

import XmBindIdSdk from '@transmitsecurity/bindid-react-native';
import type { XmBindIdExchangeTokenResponse } from "@transmitsecurity/bindid-react-native/src/transmit-bind-id-api";

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

## Troubleshooting

#### M1 arch chip

1. If you have an issue running the example project on the M1 arch chip, There is a workaround solution from Apple, [Apple Rosetta](https://support.apple.com/en-us/HT211861) that enables a Mac with Apple silicon to use apps built for a Mac with an Intel processor.

2. There is another solution by excluding arm64 for the simulator architecture, both from your project and the Pod project. [Stackoverflow](https://stackoverflow.com/a/63955114).


#### Android compile issues

If you have an issue running the example project on Android, May you should change in the example app `package.json` file the **Expo** version to `44.0.6` and the **react-native** version to `0.68.2`.

