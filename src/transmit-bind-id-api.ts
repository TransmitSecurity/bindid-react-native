/**
 * Copyright (C) Transmit Security, LTD - All Rights Reserved 
 * Unauthorized copying of this file, via any medium is strictly prohibited 
 * Proprietary and confidential 
 * Generated using tsidlc. 
 */


export const XmBindIdButtonClass = "xm-bind-id-button";
export const XmBindIdButtonOnFailureCallback = "data-xm-bind-id-on-failure";
export const XmBindIdButtonOnSuccessCallback = "data-xm-bind-id-on-success";
/**
 List of icons supported by Approval request
 */
export enum XmBindIdApprovalIcons {
   
   Payment,
   
   Locations,
   
   Contract,
   
   Email,
   
   SmartPhone,
   
   Id,
   
   Edit,
   
   Calendar,
   
   Lock,
   
   Globe
}

/**
 BindId Client SDK error codes
 */
export enum XmBindIdErrorCode {
       /** Attempt to call SDK functionality without proper SDK initialization. */
   SdkNotInitialized,
       /** SDK initialized with an invalid configuration. */
   InvalidConfig,
       /** Returned when a response does not match an expected result. */
   InvalidResponse,
       /** Returned when authentication was cancelled or rejected. */
   AccessDenied,
       /** Returned when an internal server error occurred during the process. */
   ServerError,
       /** (Mobile Only) Returned when the user cancels the alert asking for permission to login to this app, or dismisses the view controller for loading the authentication webpage. */
   UserCanceled,
       /** (Mobile Only) Platform-specific errors (like a valid presentationContextProvider was not found when -start was called. Ensure this property was not nil when -start was called.) */
   PlatformError,
       /** (Mobile Only) No internet connection. */
   InternetConnection,
       /** (Mobile Only) User isn't registered. Can only happen after calling authenticateBoundUser */
   UnknownUser
}

/**
 BindID Client SDK API compatibility selection enum.
 */
export enum XmBindIdApiCompatibilityLevel {
       /** Always use the API behavior of the latest version. */
   UseLatest,
       /** Use API Level 1 behavior. */
   ApiLevel1
}

/**
 Available types for the login hint.
 */
export enum XmBindIdLoginHintType {
       /** Indicates that the login hint value is an email address */
   Email,
       /** Indicates that the login hint value is an email verifying ticket id */
   VerifiedEmailTicket
}

/**
 Available types of user identifiers used to validate that the device is bound to the Client Application for this user.
 */
export enum XmBindIdBoundToType {
       /** Indicates that the user identifier is the subject of the ID token. */
   Sub,
       /** Indicates that the user identifier is a user alias, represented in the ID token as `bindid_alias`. If used, it is highly recommended to encrypt the authentication request (via the `encrypted` parameter) to avoid potentially exposing sensitive information. */
   Alias
}

/**
 Available BindID scopes.
 */
export enum XmBindIdScopeType {
       /** Indicate that the application intends to use OIDC to verify the user's identity. This scope is required by BindID and will be added automatically by the SDK. */
   OpenId,
       /** Indicates that the application intends to ask for the user's email information. Additional email-related claims will be added to the ID Token. */
   Email,
       /** Indicates that the application intends to ask for the user's phone information. Additional phone-related claims will be added to the ID Token. */
   Phone,
       /** Indicates that the application intends to ask for user metadata across all providers integrated with BindID. The ID token will include additional claims, which provide user and device information collected in the context of all providers integrated with BindID. */
   NetworkInfo
}

/**
 Possible verifications to require during authentication.
 */
export enum XmRequiredVerifications {
       /** If specified, BindID will attempt to ensure that the user's phone number is verified.If it's not, it will be verified during the login process. */
   Phone,
       /** If specified, BindID will attempt to ensure that the user's email address is verified. If it's not, it will be verified during the login process.*/
   Email
}

/**
 Available BindID Server Environment.
 */
export enum XmBindIdServerEnvironmentMode {
       /** Indicate the production server URL path */
   Production,
       /** Indicate the sandbox server URL path */
   Sandbox,
   
   Other
}

export interface XmBindIdError { 


/**
 Error code representing what happened
 */
readonly code: XmBindIdErrorCode;


/**
 Error description
 */
readonly message: string;


/**
 A URI identifying a human-readable web page containing information about the error, if available.
 */
readonly errorUri?: string | null;
}
/**
 Available BindID Server URL.
 */
export interface XmBindIdServerEnvironment { 


/**
 BindID Server Environment.
 */
environmentMode: XmBindIdServerEnvironmentMode;


/**
 BindID Server URL.
 */
environmentUrl: string;

/**
 Creates an instance for the sandbox or production environment, and sets the relevant server URL.
 */

/**
 Creates an instance for a custom server environment by specifying the server URL.
 */
}
/**
 Application-wide configuration for the BindID Client SDK.
 */
export interface XmBindIdConfig { 


/**
 Server URL for the BindID authentication.
 */
serverEnvironment: XmBindIdServerEnvironment;


/**
 BindID client ID as provisioned with the BindID service.
 */
clientId: string;


/**
 API Compatibility Level to use for the SDK.
 By fixing this value to a given API Level, SDK clients can signal that they want the
 SDK calls to behave as they behaved in that given API level. While this means
 that SDK behavior doesn't change, it also means that the applications may not benefit
 from updated SDK behavior. Clients are thus expected to periodically update the SDK
 API level they're interested in.
 If not provided, assumed level is 'latest'.
 */
apiCompat?: XmBindIdApiCompatibilityLevel | null;


/**
 A flag indicating whether state sent on BindID Client SDK authenticate call
 is validated during processRedirectResponse.
 */
disableStateValidation?: boolean | null;
}
/**
 A URL and title for a "more options" link to display as part of the QR authentication screen.
 If not provided, no link will be displayed. The URL must begin with "https://" and its prefix must match one that's provided during BindID enrollment.
 */
export interface XmBindIdOtherLoginOptions { 


title: string;


url: string;

}
/**
 Type and value for the login hint, which is used as a hint for the user’s login identifier (e.g., their email address)
 */
export interface XmBindIdLoginHint { 


type: XmBindIdLoginHintType;


value: string;

}
/**
 Used to require an authenticating device bound to the Client Application for a specified user (e.g., for step-up authentication). This bound status is reflected in the ID token by the `ts.bindid.app_bound_cred` ACR value, which is set using a session-feedback request.
 */
export interface XmBindIdBoundTo { 


type: XmBindIdBoundToType;


value: string;

}
/**
 Shared parameters for a BindID request configuration.
 */
export interface XmBindIdRequest { 


/**
 URL to which BindID will redirect on process completion, to convey results back to the
 calling application.
 */
redirectUri: string;


/**
 Optional. A nonce value to be included in the generated ID Token. This is typically provided by the
 application backend, and can be used to ensure at the backend that the authentication
 response corresponds to a specific request issued by the application.
 */
nonce?: string | null;


/**
 Optional. A state value to be included in the BindID response issued through redirect. This is
 typically generated at the front-end, and verified at the front-end upon processing the redirect.
 This ensures that the redirect request corresponds to the BindID authentication request.
 */
state?: string | null;


/**
 Optional. A set of BindID scopes that will include additional information in the result claims.
 If not provided, only 'Openid' scope is sent
 */
scope?: Array<XmBindIdScopeType> | null;


/**
 A collection of verifications to try and execute for this request. It is not guaranteed that
 each requested verification will be fulfilled. The `acr` claim of the resulting access token
 should be examined to determine which verifications were fulfilled.
 */
verifications?: Array<XmRequiredVerifications> | null;


/**
 Optional. A custom message to present as part of the authentication context detail screen.
 */
customMessage?: string | null;


/**
 Indicates if the BindID request will initiate a PKCE flow. In that case, the SDK will generate a code challenge using the S256 hashing algorithm and return the code verifier that can be used for a token exchange. Default is True.
 */
usePkce: boolean;


/**
 A flag indicates whether the authentication request should be encrypted.
 */
encrypted?: boolean | null;


/**
 Optional. Type and value for the login hint, which is used as a hint for the user’s login identifier (e.g., their email address)
 */
loginHint?: XmBindIdLoginHint | null;


/**
 Used to require an authenticating device bound to the Client Application for a specified user (e.g., for step-up authentication). This bound status is reflected in the ID token by the `ts.bindid.app_bound_cred` ACR value, which is set using a session-feedback request.
 */
boundTo?: XmBindIdBoundTo | null;
}
/**
 Configuration for a BindID authentication request.
 */
export interface XmBindIdAuthenticationRequest extends XmBindIdRequest { 


/**
 Optional. A URL and title for a "more options" link to display as part of the QR authentication screen.
 If not provided, no link will be displayed. The URL must begin with "https://" and its prefix must match one that's provided during BindID enrollment.
 */
otherLoginOptions?: XmBindIdOtherLoginOptions | null;

/**
 Creates an instance for an authentication request with the redirect URI and with an option to set encryption.
 */

/**
 Creates an instance of the authentication request with the redirect URI, without encryption.
 */
}
/**
 (Mobile Only)
 Configuration for a BindID authentication request.
 */
export interface XmBindIdBoundUserAuthenticationRequest extends XmBindIdRequest { 


/**
 Timeout in seconds
 Default is 10.
 */
timeout: number;


/**
 Should display loading indicator
 Default is false.
 */
displayLoader: number;

/**
 Creates an instance for an authentication request with the redirect URI and with an option to set encryption.
 */

/**
 Creates an instance of the authentication request with the redirect URI, without encryption.
 */
}
/**
 * The data that will be displayed to the user in the transaction consent.
 */
export interface XmBindIdTransactionSigningDisplayData { 


payee: string;


paymentAmount: string;


paymentMethod: string;

}
/**
 * A transaction signing request data object.
 */
export interface XmBindIdTransactionSigningData { 


/**
 Required. the data that will be displayed to the user in the transaction consent.
 */
displayData: XmBindIdTransactionSigningDisplayData;


/**
 Optional. additional data about the transaction, this data will not be displayed to the
 user, however it will be returned as part of the id token claim.
 */
additionalData?: object | null;


/**
 (default is true). Whether this claim is Essential or Voluntary.
 */
essential?: boolean | null;

}
export interface XmBindIdTransactionSigningRequest extends XmBindIdRequest { 


/**
 Required. A transaction signing request data, the data will be sent as a custom OIDC claim under the
 name bindid_psd2_transaction, it will be included in the id_token.
 */
transactionSigningData: XmBindIdTransactionSigningData;

/**
 Creates an instance of the transaction signing request with the redirect URI, an option to set encryption, and the transaction signing details.
 */

/**
 Creates an instance of the transaction signing request with the redirect URI, and transaction signing data. Encrypted will be set to True for this request instance.
 */
}
export enum XmBindIdTokenType {
       /** Indicate a Bearer access token.*/
   Bearer
}

/**
 Represents that result of an exchange token request.
 */
export interface XmBindIdExchangeTokenResponse { 


/**
 Token-based authentication parameter - Allows an application to access an API.
 The application receives an access token after a user
 successfully authenticates and authorizes access,
 then passes the access token as a credential when it calls the target API.
 */
readonly accessToken: string;


/**
 Token-based authentication parameter - Used to cache user profile information
 represented in the form of claims.
 */
readonly idToken: string;


/**
 Type of access token. Currently, only “Bearer” is returned.
 */
readonly tokenType: XmBindIdTokenType;


/**
 Expiration time of the access token in seconds.
 */
readonly expiresIn: number;
}
/**
 Represents that result of an authentication request submitted to BindID.
 */
export interface XmBindIdResponse { 


/**
 Authorization code returned by BindID. This can be used to obtain
 the resulting ID Token and Access Token by invoking the
 token endpoint on the BindID OAuth API.
 This value is typically sent to application backend where it is
 exchanged for the sensitive Access Token.
 */
readonly code: string;


/**
 Optional. The state value returned from the BindID process. This must match the state
 passed in the BindID invocation request, if one was passed.
 */
readonly state?: string | null;


/**
 Code verifier to use for a PKCE token exchange, provided when the “usePkce” request parameter is set to True.
 */
readonly codeVerifier?: string | null;


/**
 Redirect URI to use for a PKCE token exchange, which corresponds to the URI passed in the request.
 */
readonly redirectUri: string;
}
export interface XmBindIdExchangeTokenRequest { 


readonly codeVerifier?: string | null;


readonly code: string;


readonly redirectUri: string;

/**
 Creates an instance of the exchange token request with the response of the authenticate() or signTransaction() call (if the “usePkce” request parameter was set to True). The response includes the code verifier, authorization code, and redirect URI required for the token exchange.
 */
}
export interface XmBindIdApprovalBaseAttribute { 


/**
 Required. Attribute label.
 */
label: string;


/**
 Required. Attribute value.
 */
value: string;

}
export interface XmBindIdApprovalAttribute extends XmBindIdApprovalBaseAttribute { 


icon: XmBindIdApprovalIcons;

}
/**
 Required. The data that will be displayed to the user in the approval screen.
 */
export interface XmBindIdApprovalSigningDisplayData { 


/**
 Optional. Represents the main attribute to display to the user on the approval screen, which is
 emphasized in the UI. For example, this attribute may display the supplier account name to approve
 an update to their payment details.
 */
mainAttribute?: XmBindIdApprovalBaseAttribute | null;


/**
 Represents attributes to display to the user on the approval screen. For example,
 this may include the supplier's account number and bank name for an update to their payment details.
 Up to two different attributes may be defined for this list.
 */
attributes: Array<XmBindIdApprovalAttribute>;

}
/**
 Required. Approval request data, which will be returned in the ID token as a custom OIDC claim named
 bindid_approval.
 */
export interface XmBindIdApprovalSigningData { 


/**
 Required. The data that will be displayed to the user in the approval screen.
 */
displayData: XmBindIdApprovalSigningDisplayData;


/**
 Optional. Additional data about the approval. This data will not be displayed to the
 user, however it will be returned as part of the ID token claim.
 */
additionalData?: object | null;


/**
 (default is true). Whether this claim is Essential or Voluntary.
 */
essential?: boolean | null;

}
/**
 An approval signing request data object.
 */
export interface XmBindIdApprovalSigningRequest extends XmBindIdRequest { 


approvalSigningData: XmBindIdApprovalSigningData;

}
/**
 Entry point interface to the BindID Client SDK.
 
 This interface declares all top level services supported by the SDK. An instance of this interface is available
 to client applications as window.XmBindId.
 
 Before the BindID SDK can be used, the {@link XmBindIdSdk.initialize} call must be invoked, with proper configuration for the client
 application using BindID.
 */
export interface XmBindIdSdk { 

   /**
 Initialize the BindID Client SDK and set application-wide configuration.
 
 All BindID Client SDK calls must be invoked only after succesful completion of this asynchornous
 initialization call.
 
 The returned promise will either be resolved with a boolean 'true' value, or rejected with an error.
 
 * @return 
 */
initialize(config: XmBindIdConfig) : Promise<boolean>;

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
authenticate(bindIdRequestParams: XmBindIdAuthenticationRequest) : Promise<XmBindIdResponse>;

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
authenticateBoundUser(bindIdRequestParams: XmBindIdBoundUserAuthenticationRequest) : Promise<XmBindIdResponse>;

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
signTransaction(bindIdTransactionRequest: XmBindIdTransactionSigningRequest) : Promise<XmBindIdResponse>;

   /**
 Invoke a PKCE token exchange using the BindID SDK. The response will include the ID and access tokens.
 
 * @return 
 */
exchangeToken(exchangeRequest: XmBindIdExchangeTokenRequest) : Promise<XmBindIdExchangeTokenResponse>;

   /**
 Process a BindID redirect response.
 
 BindID reports results back to the service provider by redirecting the user agent to a service-provider
 provided redirect URL, attaching the authentication response.
 
 This function should be invoked by the BindID client application in response to this redirect. It
 processes the redirect URL, parses and extracts the BindID response from it.
 
 The returned Promise will either be rejected with an error, or complete with an object providing access
 * @return 
 */
processRedirectResponse() : Promise<XmBindIdResponse>;

   /**
 Invoke BindID authentication flow for approval signing.
 
 This request will behave similarly to an authentication request, with the following additions:
 
 OIDC claims parameter will be added to the request to indicate to the BindID server that this is an approval
 signing request.
 By default the request will be sent as an encrypted JWT.
 BindID server will show the approval details to the user.
 BindID server will include the approval claim in the ID token upon successful authentication and token exchange.
 
 * @return 
 */
invokeApproval(bindIdApprovalRequest: XmBindIdApprovalSigningRequest) : Promise<XmBindIdResponse>;
}
