# Login

Build app-less, passwordless login experiences with BindID for customers that want to access your React Native (iOS/Android) application. This sample app wraps the BindID SDK for iOS and the the BindID SDK for Android in a React Native Module to initiate strong authentication flows with the BindID service to sign in your users.

## Prerequisites

Before you begin, you'll need to have an application configured in the [BindID Admin Portal](https://admin.bindid-sandbox.io/console/#/applications). From the application settings, obtain the client credentials and configure a redirect URI for this client that will receive the authentication result.

- Example ClientID: `XXXXXXX.XXXXXXXX.dev_6fa9320b.bindid.io"` (This is auto generated in the console)
- Example RedirectURI: `rnbindidexample://login`

For more, see [BindID Admin Portal: Get Started](https://developer.bindid.io/docs/guides/admin_portal/topics/getStarted/get_started_admin_portal).

-----------

## Instructions

Before running the application on your iOS or Android devices you will need to configure the application.

Configure `env.ts`:
Open `example/src/env.ts` and setup your ClientID and Redirect URI
```bash
'ClientID' # Client ID obtained from the BindID Admin Portal
'RedirectURI' # Redirect URI you defined in the BindID Admin Portal
```

#### iOS Setup:
1. Open `example/ios/BindidReactNativeExample.xcworkspace` in XCode.
2. Select the workspace (`BindidReactNativeExample`) and then select the `info` tab.
3. Add a URL Type and set the `URL Schemes` to be your `RedirectURI` (example `rnbindidexample://login`).

#### Android Setup:
1. Open `example/android` in Android studio.
2. Open `BindidReactNativeExample/app/src/main/res/values/strings.xml`.
3. Change `bid_scheme` and `bid_host` according to your `RedirectURI`.

Based on the `rnbindidexample://login` example, `bid_scheme` should be `rnbindidexample` and `bid_host` should be `login`

#### Run the application
1. Run `npm install` in the project root folder and in the `example` folder.
2. Run `pod install` in `example/ios`
3. In `example` folder, run `npx react-native run-ios` or `npx react-native run-android` 

-----------

## What is BindID?
The BindID service is an app-less, strong portable authenticator offered by Transmit Security. BindID uses FIDO-based biometrics for secure, frictionless, and consistent customer authentication. With one click to create new accounts or sign into existing ones, BindID eliminates passwords and the inconveniences of traditional credential-based logins.  
[Learn more about how you can boost your experiences with BindID.](https://www.transmitsecurity.com/developer)

## Author
Transmit Security, https://github.com/TransmitSecurity

## License
This project is licensed under the MIT license. See the LICENSE file for more info.

# Special Notes

### Android BindID SDK folder structure:
- The BindID SDK should be in the app root android folder (`example/android/`)
- The BindID SDK folder structure should be `com/ts/bindid/`
- Configure SDK path for the app (example): `build.gradle` -> all projects -> `repositories: maven { url "file:///${rootDir}" }`
- Configure SDK path for the module (bindidreactnative): build.gradle, dependencies: `implementation ("com.ts:bindid:1.15.0@aar") { transitive=true }`

### iOS BindID SDK
- Is configured in the `bindid-react-native.podspec` file as a dependency feched from `https://www.transmitsecurity.com.git`

### Disclaimer
This example project is for reference only and is not intended to represent the best or only approach to any particular issue