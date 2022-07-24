package com.bindidreactnative;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bindidreactnative.jwtUtils.FetchPublicKey;
import com.bindidreactnative.jwtUtils.ParseIDToken;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.ts.bindid.XmBindIdApiCompatibilityLevel;
import com.ts.bindid.XmBindIdAuthenticationRequest;
import com.ts.bindid.XmBindIdBoundUserAuthenticationRequest;
import com.ts.bindid.XmBindIdTransactionSigningRequest;
import com.ts.bindid.XmBindIdConfig;
import com.ts.bindid.XmBindIdError;
import com.ts.bindid.XmBindIdErrorCode;
import com.ts.bindid.XmBindIdExchangeTokenRequest;
import com.ts.bindid.XmBindIdExchangeTokenResponse;
import com.ts.bindid.XmBindIdResponse;
import com.ts.bindid.XmBindIdScopeType;
import com.ts.bindid.XmBindIdSdk;
import com.ts.bindid.XmBindIdServerEnvironment;
import com.ts.bindid.XmBindIdServerEnvironmentMode;
import com.ts.bindid.impl.XmBindIdConfigImpl;
import com.ts.bindid.impl.XmBindIdServerEnvironmentImpl;
import com.ts.bindid.util.ObservableFuture;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

@ReactModule(name = BindidReactNativeModule.NAME)
public class BindidReactNativeModule extends ReactContextBaseJavaModule {

  public static final String NAME = "BindidReactNative";
  private ReactApplicationContext reactContext;
  private String host;
  private TSBindIdParser parser = new TSBindIdParser();

  public BindidReactNativeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  // Public API

  @ReactMethod
  public void initialize(ReadableMap config, Promise promise) {
    runOnUiThread(() -> onInitializeSDK(config, reactContext, promise));
  }

  @ReactMethod
  public void authenticate(ReadableMap bindIdRequest, Promise promise) {
    runOnUiThread(() -> onAuthenticate(bindIdRequest, promise));
  }

  @ReactMethod
  public void signTransaction(ReadableMap bindIdTransactionRequest, Promise promise) {
    runOnUiThread(() -> onSignTransaction(bindIdTransactionRequest, promise));
  }

  @ReactMethod
  public void exchangeToken(ReadableMap exchangeRequest, Promise promise) {
    runOnUiThread(() -> exchange(exchangeRequest, promise));
  }

  @ReactMethod
  public void authenticateBoundUser(ReadableMap bindIdRequest, Promise promise) {
    runOnUiThread(() -> onAuthenticateBoundUser(bindIdRequest, promise));
  }

  @ReactMethod
  public void validate(String idToken, String hostName, Promise promise) {
    runOnUiThread(() -> onValidate(idToken, hostName, promise));
  }

   @ReactMethod
   public void parse(String idToken, Promise promise) {
     runOnUiThread(() -> onParse(idToken, promise));
   }

  // Private Implementation

  private void onInitializeSDK(ReadableMap config, Context applicationContext, Promise promise) {

    XmBindIdConfig configuration = parser.getConfig(applicationContext, config);

    XmBindIdSdk.getInstance().initialize(configuration)
      .addListener(new ObservableFuture.Listener<Boolean, XmBindIdError>() {

        @Override
        public void onComplete(@NotNull Boolean aBoolean) {
          logMessage("BindID Initialized");
          promise.resolve("BindID initialized with success");
        }

        @Override
        public void onReject(@NotNull XmBindIdError xmBindIdError) {
          logError("SDK failed to initialize: " + xmBindIdError.getCode() + "\n" + xmBindIdError.getMessage());
          promise.reject(xmBindIdError);
        }
      });
  }

  private void onAuthenticate(ReadableMap bindIdRequest, Promise promise) {

    XmBindIdAuthenticationRequest authenticationRequest = parser.getAuthenticationRequest(bindIdRequest);

    XmBindIdSdk.getInstance().authenticate(authenticationRequest)
      .addListener(new ObservableFuture.Listener<XmBindIdResponse, XmBindIdError>() {
        @Override
        public void onComplete(XmBindIdResponse xmBindIdResponse) {
          promise.resolve(parser.buildWriteableMap(xmBindIdResponse));
        }

        @Override
        public void onReject(XmBindIdError xmBindIdError) {
          promise.reject(xmBindIdError.getCode().name(), parser.buildWriteableMap(xmBindIdError));
        }
      });
  }

  private void onAuthenticateBoundUser(ReadableMap bindIdRequest, Promise promise) {

    XmBindIdBoundUserAuthenticationRequest authenticationRequest = parser.getBoundUserAuthenticationRequest(bindIdRequest);

    XmBindIdSdk.getInstance().authenticateBoundUser(authenticationRequest)
      .addListener(new ObservableFuture.Listener<XmBindIdResponse, XmBindIdError>() {
        @Override
        public void onComplete(XmBindIdResponse xmBindIdResponse) {
          promise.resolve(parser.buildWriteableMap(xmBindIdResponse));
        }

        @Override
        public void onReject(XmBindIdError xmBindIdError) {
          promise.reject(xmBindIdError.getCode().name(), parser.buildWriteableMap(xmBindIdError));
        }
      });
  }

  private void onSignTransaction(ReadableMap bindIdTransactionRequest, Promise promise) {

    XmBindIdTransactionSigningRequest request = parser.getTransactionSigningRequest(bindIdTransactionRequest);

    XmBindIdSdk.getInstance().signTransaction(request)
      .addListener(new ObservableFuture.Listener<XmBindIdResponse, XmBindIdError>() {
        @Override
        public void onComplete(XmBindIdResponse xmBindIdResponse) {
          promise.resolve(parser.buildWriteableMap(xmBindIdResponse));
        }

        @Override
        public void onReject(XmBindIdError xmBindIdError) {
          promise.reject(xmBindIdError.getCode().name(), parser.buildWriteableMap(xmBindIdError));
        }
      });
  }

  private void exchange(ReadableMap exchangeRequest, Promise promise) {

    XmBindIdExchangeTokenRequest request = parser.getExchangeToken(exchangeRequest);

    XmBindIdSdk.getInstance().exchangeToken(request).addListener(new ObservableFuture.Listener<XmBindIdExchangeTokenResponse, XmBindIdError>() {
      @Override
      public void onComplete(XmBindIdExchangeTokenResponse xmBindIdExchangeTokenResponse) {
        promise.resolve(parser.buildWriteableMap(xmBindIdExchangeTokenResponse));
      }

      @Override
      public void onReject(XmBindIdError xmBindIdError) {
        promise.reject(xmBindIdError.getCode().name(), parser.buildWriteableMap(xmBindIdError));
      }
    });
  }

  private void onValidate(String idToken, String hostName, Promise promise) {
    // Validate the tokenResponse
    // 1. get publicKey from BindID server
    // 2. validate JWT
    FetchPublicKey.fetchPublicKey(hostName, new FetchPublicKey.fetchBindIDPublicKeyListener() {
      @Override
      public void onResponse(String publicKey) {
        verifyIDToken(idToken, publicKey, promise);
      }

      @Override
      public void onFailure(String error) {
        promise.reject(new Error(error));
      }
    });
  }

  private void verifyIDToken(String idToken, String publicKey, Promise promise) {
    try {
      boolean isValid = SignedJWT.parse(idToken)
        .verify(new RSASSAVerifier(RSAKey.parse(publicKey)));

      WritableMap results = Arguments.createMap();
      results.putBoolean("isValid", isValid);
      promise.resolve(results);

    } catch (Exception e) {
      promise.reject(e);
    }
  }

   private void onParse(String idToken, Promise promise) {
     HashMap<String, Object> parsed = ParseIDToken.parse(idToken);
     Boolean isSuccess = (Boolean) parsed.get(ParseIDToken.ResponseSuccessKey);

     if (isSuccess) {
       WritableMap passport = (WritableMap) parsed.get(ParseIDToken.ResponseParsedTokenKey);
       promise.resolve(passport);
     } else {
       promise.reject(new Error("Error parsing ID Token"));
     }

   }


  private void logMessage(String message) {
    Log.d(NAME, message);
  }

  private void logError(String message) {
    Log.e(NAME, message);
  }


}
