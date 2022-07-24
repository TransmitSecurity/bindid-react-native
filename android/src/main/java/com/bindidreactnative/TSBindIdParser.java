package com.bindidreactnative;

import android.content.Context;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ts.bindid.XmBindIdApiCompatibilityLevel;
import com.ts.bindid.XmBindIdAuthenticationRequest;
import com.ts.bindid.XmBindIdBoundTo;
import com.ts.bindid.XmBindIdBoundToType;
import com.ts.bindid.XmBindIdBoundUserAuthenticationRequest;
import com.ts.bindid.XmBindIdConfig;
import com.ts.bindid.XmBindIdError;
import com.ts.bindid.XmBindIdExchangeTokenRequest;
import com.ts.bindid.XmBindIdExchangeTokenResponse;
import com.ts.bindid.XmBindIdLoginHint;
import com.ts.bindid.XmBindIdLoginHintType;
import com.ts.bindid.XmBindIdResponse;
import com.ts.bindid.XmBindIdScopeType;
import com.ts.bindid.XmBindIdServerEnvironment;
import com.ts.bindid.XmBindIdServerEnvironmentMode;
import com.ts.bindid.XmBindIdTransactionSigningData;
import com.ts.bindid.XmBindIdTransactionSigningDisplayData;
import com.ts.bindid.XmBindIdTransactionSigningRequest;
import com.ts.bindid.XmRequiredVerifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TSBindIdParser {

  public XmBindIdConfig getConfig(Context applicationContext, ReadableMap map) {

    String clientId = map.getString("clientId");

    ReadableMap environment = map.getMap("serverEnvironment");
    String environmentUrl = environment.getString("environmentUrl");
    XmBindIdServerEnvironmentMode environmentMode = TSBindIdUtils.getEnum(environment, "environmentMode", XmBindIdServerEnvironmentMode.class);

    XmBindIdServerEnvironment serverEnvironment = XmBindIdServerEnvironment.createWithMode(environmentMode);
    serverEnvironment.setEnvironmentUrl(environmentUrl);

    XmBindIdConfig config = XmBindIdConfig.create(applicationContext.getApplicationContext(), serverEnvironment, clientId);

    if (map.hasKey("disableStateValidation")) {
      config.setDisableStateValidation(map.getBoolean("disableStateValidation"));
    }

    XmBindIdApiCompatibilityLevel apiCompat = TSBindIdUtils.getEnum(map, "apiCompat", XmBindIdApiCompatibilityLevel.class);
    config.setApiCompat(apiCompat);


    return config;
  }


  public XmBindIdAuthenticationRequest getAuthenticationRequest(ReadableMap map) {

    String redirectURI = map.getString("redirectUri");

    XmBindIdAuthenticationRequest request = XmBindIdAuthenticationRequest.create(redirectURI);


    Boolean usePkce = map.getBoolean("usePkce");
    String nonce = map.getString("nonce");
    String state = map.getString("state");
    String customMessage = map.getString("customMessage");


    if (map.hasKey("scope")) {
      ReadableArray scopes = map.getArray("scope");
      ArrayList<XmBindIdScopeType> scopesList = new ArrayList<>();
      for (int i = 0; i < scopes.size(); i++) {
        Integer index = scopes.getInt(i);
        XmBindIdScopeType scopeType = XmBindIdScopeType.values()[index];
        scopesList.add(scopeType);
      }

      request.setScope(scopesList);

    }

    if (map.hasKey("encrypted")) {
      request.setEncrypted(map.getBoolean("encrypted"));
    }

    request.setUsePkce(usePkce);
    request.setCustomMessage(customMessage);
    request.setNonce(nonce);
    request.setState(state);


    ReadableMap loginHintMap = map.getMap("loginHint");
    if(loginHintMap != null) {
      String loginHintValue = loginHintMap.getString("value");
      XmBindIdLoginHintType loginHintType = TSBindIdUtils.getEnum(loginHintMap, "type", XmBindIdLoginHintType.class);
      if (loginHintType != null && loginHintValue != null) {
        XmBindIdLoginHint loginHint = XmBindIdLoginHint.create(loginHintType, loginHintValue);
        request.setLoginHint(loginHint);
      }
    }

    ReadableMap boundToMap = map.getMap("boundTo");
    if (boundToMap != null) {
      String boundToValue = boundToMap.getString("value");
      XmBindIdBoundToType boundToType = TSBindIdUtils.getEnum(boundToMap, "type", XmBindIdBoundToType.class);
      if (boundToType != null && boundToValue != null) {
        XmBindIdBoundTo boundTo = XmBindIdBoundTo.create(boundToType, boundToValue);
        request.setBoundTo(boundTo);
      }
    }


    return request;

  }


  public XmBindIdTransactionSigningRequest getTransactionSigningRequest(ReadableMap map) {

    XmBindIdAuthenticationRequest request = getAuthenticationRequest(map);

    ReadableMap transactionSigningDataMap = map.getMap("transactionSigningData");

    ReadableMap transactionSigningDisplayDataMap = transactionSigningDataMap.getMap("displayData");
    String mPayee = transactionSigningDisplayDataMap.getString("payee");
    String mPaymentAmount = transactionSigningDisplayDataMap.getString("paymentAmount");
    String mPaymentMethod = transactionSigningDisplayDataMap.getString("paymentMethod");

    XmBindIdTransactionSigningDisplayData mDisplayData = XmBindIdTransactionSigningDisplayData.create(mPayee, mPaymentAmount, mPaymentMethod);

    XmBindIdTransactionSigningData transactionSigningData = XmBindIdTransactionSigningData.create(mDisplayData);

    if (transactionSigningDataMap.hasKey("additionalData")) {
      transactionSigningData.setAdditionalData(transactionSigningDataMap.getMap("additionalData").toHashMap());
    }

    if (transactionSigningDataMap.hasKey("essential")) {
      transactionSigningData.setEssential(transactionSigningDataMap.getBoolean("essential"));
    }

    XmBindIdTransactionSigningRequest transactionSigningRequest = XmBindIdTransactionSigningRequest.create(request.getRedirectUri(), request.getEncrypted(), transactionSigningData);
    transactionSigningRequest.setUsePkce(request.getUsePkce());
    transactionSigningRequest.setCustomMessage(request.getCustomMessage());
    transactionSigningRequest.setNonce(request.getNonce());
    transactionSigningRequest.setState(request.getState());
    transactionSigningRequest.setScope(request.getScope());
    transactionSigningRequest.setBoundTo(request.getBoundTo());
    transactionSigningRequest.setLoginHint(request.getLoginHint());


    return transactionSigningRequest;

  }


  public XmBindIdBoundUserAuthenticationRequest getBoundUserAuthenticationRequest(ReadableMap map) {

    XmBindIdAuthenticationRequest request = getAuthenticationRequest(map);

    XmBindIdBoundUserAuthenticationRequest boundUserAuthenticationRequest = XmBindIdBoundUserAuthenticationRequest.create(request.getRedirectUri());
    boundUserAuthenticationRequest.setUsePkce(request.getUsePkce());
    boundUserAuthenticationRequest.setCustomMessage(request.getCustomMessage());
    boundUserAuthenticationRequest.setNonce(request.getNonce());
    boundUserAuthenticationRequest.setState(request.getState());
    boundUserAuthenticationRequest.setScope(request.getScope());
    boundUserAuthenticationRequest.setBoundTo(request.getBoundTo());
    boundUserAuthenticationRequest.setLoginHint(request.getLoginHint());


    if (map.hasKey("timeout")) {
      boundUserAuthenticationRequest.setTimeout(map.getInt("timeout"));
    }

    if (map.hasKey("displayLoader")) {
      boundUserAuthenticationRequest.setDisplayLoader(map.getInt("displayLoader"));
    }

    return boundUserAuthenticationRequest;

  }


  public XmBindIdExchangeTokenRequest getExchangeToken(ReadableMap map) {

    XmBindIdResponseClass response = getBindIdResponse(map);
    XmBindIdExchangeTokenRequest request = XmBindIdExchangeTokenRequest.create(response);
    return request;
  }

  public XmBindIdResponseClass getBindIdResponse(ReadableMap map) {

    String code = map.getString("code");
    String state = map.getString("state");
    String codeVerifier = map.getString("codeVerifier");
    String redirectUri = map.getString("redirectUri");

    XmBindIdResponseClass response = new XmBindIdResponseClass(code, state, codeVerifier, redirectUri);
    return response;
  }


  public <T> WritableMap buildWriteableMap(T obj)  {
    try {
      JSONObject json = TSBindIdUtils.objectToJSONObject(obj);
      return TSBindIdUtils.convertJsonToMap(json);
    } catch (JSONException | JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }

  }

  public class XmBindIdResponseClass implements XmBindIdResponse {

    private String code;
    private String state;
    private String codeVerifier;
    private String redirectUri;

    public XmBindIdResponseClass(String code, String state, String codeVerifier, String redirectUri) {
      this.code = code;
      this.state = state;
      this.codeVerifier = codeVerifier;
      this.redirectUri = redirectUri;
    }

    @Override
    public String getCode() {
      return code;
    }

    @Nullable
    @Override
    public String getState() {
      return state;
    }

    @Nullable
    @Override
    public String getCodeVerifier() {
      return codeVerifier;
    }

    @Override
    public String getRedirectUri() {
      return redirectUri;
    }
  }

}


