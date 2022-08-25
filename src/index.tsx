import { NativeModules, Platform } from 'react-native';
import type {
  XmBindIdAuthenticationRequest,
  XmBindIdConfig,
  XmBindIdResponse,
  XmBindIdTransactionSigningRequest,
  XmBindIdExchangeTokenRequest,
  XmBindIdExchangeTokenResponse,
  XmBindIdBoundUserAuthenticationRequest
} from "./transmit-bind-id-api";

const LINKING_ERROR =
  `The package 'bindid-react-native' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const BindidReactNative = NativeModules.BindidReactNative
  ? NativeModules.BindidReactNative
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/** BindID SDK */

export interface BindIDValidationResponse { isValid: boolean }
export interface BindIDParseResponse { response: any }

/** Module API */

/**
  Entry point interface to the BindID Client SDK.
  
  This interface declares all top level services supported by the SDK. An instance of this interface is available
  to client applications as window.XmBindId.
  
  Before the BindID SDK can be used, the {@link XmBindIdSdk.initialize} call must be invoked, with proper configuration for the client
  application using BindID.
*/
class XmBindIdSdk {

 /**
    Initialize the BindID Client SDK and set application-wide configuration.
    
    All BindID Client SDK calls must be invoked only after succesful completion of this asynchornous
    initialization call.
    
    The returned promise will either be resolved with a boolean 'true' value, or rejected with an error.
    
    * @return 
  */
  initialize = (config: XmBindIdConfig): Promise<boolean> => {
    return BindidReactNative.initialize(config);
  }

 /**
    Invoke a BindID authentication flow.
    
    This call will start a user login flow using BindID. The browser will be redirected to BindID's
    login page, where user identity will be established. Once complete, BindID will redirect the user
    back to the invoking web/mobile application based on the URL provided in the authentication request parameters,
    sending back authentication result information.
    
    If invoked by a web application, the web application should invoke
    #processRedirectResponse to extract the information returned in the response and complete the authentication
    process.
    
    The returned Promise will either be rejected with an error, or never complete (since this call may redirect
    the user agent to another page).
    
    * @return 
  */
  authenticate = (bindIdRequest: XmBindIdAuthenticationRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.authenticate(bindIdRequest);
  }

 /**
    Invoke BindID authentication flow for transaction signing.
    
    This request will behave similarly to an authentication request, with the following additions:
    
    OIDC claims parameter will be added to the request to indicate the BindID server that this is a transaction
    signing request.
    By default the request will be sent as an encrypted JWT.
    BindID server will show the transaction details to the user.
    BindID server will include the transaction claim in the id_token upon successful authentication and token exchange.
    
    * @return 
  */
  signTransaction = (bindIdTransactionRequest: XmBindIdTransactionSigningRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.signTransaction(bindIdTransactionRequest);
  }

 /**
    Invoke a BindID authentication flow using native mobile biometrics.
    This call will start a user login flow using BindID only if native biometric authentication can be performed;
    otherwise, an error will be returned.
    
    The native flow comprises only of providing a biometric sample (e.g., fingerprint),
    and is not available when other actions are required to process the authentication request (e.g., for new device registration using FIDO2 biometrics).
    
    Once complete, BindID will redirect the user back to the invoking mobile application based on the URL provided in the authentication request parameters,
    sending back authentication result information.
    
    * @return 
  */
  authenticateWithBoundUser = (bindIdRequest: XmBindIdBoundUserAuthenticationRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.authenticateWithBoundUser(bindIdRequest);
  }

 /**
     Invoke a PKCE token exchange using the BindID SDK. The response will include the ID and access tokens.

     * @return 
  */
  exchangeToken = (exchangeRequest: XmBindIdExchangeTokenRequest): Promise<XmBindIdExchangeTokenResponse> => {
    return BindidReactNative.exchangeToken(exchangeRequest);
  }

  /**
     Invoke a validate API for id_token validation, The response will be true if valid else false.
     Note: The hostName should be the domain server without the scheme (example: signin.bindid-sandbox.io)

     * @return 
  */
  validate = (idToken: string, hostName: string): Promise<BindIDValidationResponse> => {
    return BindidReactNative.validate(idToken, hostName);
  }

  /**
     Invoke a parsing API to get the id_token payload data, Payload is the second part of the token, which contains the claims. 
     Claims are statements about an entity (typically, the user) and additional data.
     The response will be representing claims in JSON data format.

     * @return 
  */
  parse = (idToken: string): Promise<BindIDParseResponse> => {
    return BindidReactNative.parse(idToken);
  }

}

export default new XmBindIdSdk();