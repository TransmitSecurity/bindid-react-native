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

// export interface BindIDError { message: string }
export interface BindIDValidationResponse { isValid: boolean }
export interface BindIDParseResponse { response: any }

/** Module API */

class XmBindIdSdk {
  initialize = (config: XmBindIdConfig): Promise<boolean> => {
    return BindidReactNative.initialize(config);
  }

  authenticate = (bindIdRequest: XmBindIdAuthenticationRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.authenticate(bindIdRequest);
  }

  signTransaction = (bindIdTransactionRequest: XmBindIdTransactionSigningRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.signTransaction(bindIdTransactionRequest);
  }

  authenticateWithBoundUser = (bindIdRequest: XmBindIdBoundUserAuthenticationRequest): Promise<XmBindIdResponse> => {
    return BindidReactNative.authenticateWithBoundUser(bindIdRequest);
  }

  exchangeToken = (exchangeRequest: XmBindIdExchangeTokenRequest): Promise<XmBindIdExchangeTokenResponse> => {
    return BindidReactNative.exchangeToken(exchangeRequest);
  }

  validate = (idToken: string, hostName: string): Promise<BindIDValidationResponse> => {
    return BindidReactNative.validate(idToken, hostName);
  }

  parse = (idToken: string): Promise<BindIDParseResponse> => {
    return BindidReactNative.parse(idToken);
  }
}

export default new XmBindIdSdk();