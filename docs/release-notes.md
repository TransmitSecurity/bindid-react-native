# BindID React Native SDK Release Notes

<!---
Template
## Version - Date
### Content
#### New Features
#### Enhancements
#### Bug Fixes
### Upgrade
#### EXPECTED MIGRATION CHANGES 
--->

## 1.1.1 - July 2023
### Content
### Upgrade
1. Upgraded the iOS XmBindIdSDK framework version to 1.70.0.
1. Upgraded the Android XmBindIdSDK library version to 1.60.1.

## 1.1.0 - November 2022
### Content
#### New Features
1. Added Token exchange platform mode enum to XmBindIdServerEnvironment to indicate the platform for token exchange.
1. General bug fixes and performance improvement.

### Upgrade
1. Upgraded the iOS XmBindIdSDK framework version to 1.40.0.
1. Upgraded the Android XmBindIdSDK library version to 1.30.0.

## 1.0.2 - August 2022
### Content
#### Bug Fixes
1. Fixed an issue where the current package.json name is not a valid name for a pod.

## 1.0.1 - August 2022
### Content
### Upgrade
1. The iOS module deployment target has been raised to iOS 13.0.
1. Upgraded the iOS XmBindIdSDK framework version to 1.30.0.

#### Bug Fixes
1. General bug fixes and performance improvement.
1. Fixed an issue where the parse API crashed in parsing JWT.
1. Fixed an issue where locking the device caused infinite activity loading in iOS. 
1. Fixed an issue where saving to the Keychain with access control biometric failed on iOS simulator 15.x.

## 1.0.0 - July 2022
### Content
1. Released BindID React Native SDK version 1.0.0.
