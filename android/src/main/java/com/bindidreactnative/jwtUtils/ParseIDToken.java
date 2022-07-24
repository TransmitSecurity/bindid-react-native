package com.bindidreactnative.jwtUtils;

import com.nimbusds.jwt.SignedJWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

public class ParseIDToken {

  public final static String ResponseSuccessKey = "success";
  public final static String ResponseParsedTokenKey = "passport";

  public final static String ValueNotSet = "Not Set";

  public static HashMap<String, Object> parse(String idToken) {

    String parsedToString = parseTokenToString(idToken);
    if (parsedToString.isEmpty()) {
      return createResponse(false);
    }

    try {

      JSONObject json = new JSONObject(parsedToString);
      WritableMap parsed = Arguments.createMap();

      parsed.putString("01. User ID", json.has("sub")? json.getString("sub") : null);
      parsed.putString("02. User Alias", json.has("bindid_alias")? json.getString("bindid_alias") : ParseIDToken.ValueNotSet);
      parsed.putString("03. Email", json.has("email")? json.getString("email") : ParseIDToken.ValueNotSet);
      parsed.putString("04. Phone Number", json.has("phone_number")? json.getString("phone_number") : ParseIDToken.ValueNotSet);

      applyNetworkInfo(json, parsed);
      applyAuthenticatingDeviceConfirmed(json, parsed);
      applyBindIDInfo(json, parsed);

      HashMap<String, Object> successResponse = createResponse(true);
      successResponse.put(ResponseParsedTokenKey, parsed);
      return successResponse;
    } catch (JSONException e) {
      return createResponse(false);
    }
  }

  // Convert token parts to map

  private static void applyNetworkInfo(JSONObject json, WritableMap parsed) {
    if (!json.has("bindid_network_info")) { return; }

    try {
      JSONObject info = json.getJSONObject("bindid_network_info");
      parsed.putString("05. User Registered On", info.has("user_registration_time") ? info.getString("user_registration_time") : ParseIDToken.ValueNotSet);
      parsed.putString("09. User Last Seen by Network", info.has("user_last_seen")?  info.getString("user_last_seen") : ParseIDToken.ValueNotSet);
      parsed.putString("10. Total Providers that Confirmed User", info.has("confirmed_capp_count") ? info.getString("confirmed_capp_count") : "0");
      parsed.putString("11. Authenticating Device Registered", info.has("authenticating_device_registration_time") ? info.getString("authenticating_device_registration_time") : ParseIDToken.ValueNotSet);
      parsed.putString("14. Authenticating Device Last Seen by Network", info.has("authenticating_device_last_seen") ? info.getString("authenticating_device_last_seen") : ParseIDToken.ValueNotSet);
      parsed.putString("15. Total Known Devices", info.has("device_count") ? info.getString("device_count") : "0");

    } catch (JSONException e) {
      return;
    }
  }

  @SuppressWarnings("unchecked")
  private static void applyAuthenticatingDeviceConfirmed(JSONObject json, WritableMap parsed) {
    Boolean acrContains = false;

    try {
      if (json.has("acr")) {
        ArrayList<String> acr = (ArrayList<String>) json.get("acr");
        acrContains = acr.contains("ts.bindid.app_bound_cred");
      }
    } catch (JSONException e) { }

    parsed.putString("12. Authenticating Device Confirmed", acrContains ? "Yes" : "No");
  }

  private static void applyBindIDInfo(JSONObject json, WritableMap parsed) {
    if (!json.has("bindid_info")) { return; }

    try {
      JSONObject info = json.getJSONObject("bindid_info");
      parsed.putString("06. User First Seen", info.has("capp_first_login")? toDateString(info.getString("capp_first_login")) : ParseIDToken.ValueNotSet);
      parsed.putString("07. User First Confirmed", info.has("capp_first_confirmed_login")? toDateString(info.getString("capp_first_confirmed_login")) : ParseIDToken.ValueNotSet);
      parsed.putString("08. User Last Seen", info.has("capp_last_login") ? toDateString(info.getString("capp_last_login")) : ParseIDToken.ValueNotSet);
      parsed.putString("13. Authenticating Device Last Seen", info.has("capp_last_login_from_authenticating_device")? toDateString(info.getString("capp_last_login_from_authenticating_device")) : ParseIDToken.ValueNotSet);
    } catch (JSONException e) {
      return;
    }
  }

  // ---------------------------

  // Parse Base64 IdToken to JSON compatible String
  private static String parseTokenToString(String idToken) {
    try {
      SignedJWT jwt = SignedJWT.parse(idToken);
      return jwt.getPayload().toString();
    } catch (java.text.ParseException e) {
      return "";
    }
  }

  // Helpers

  private static String toDateString(String date) {
    DateFormat format = new SimpleDateFormat("MMM d, yyyy HH:mm a");
    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(Long.parseLong(date) * 1000);
    return format.format(cal.getTime());
  }

  private static HashMap<String, Object> createResponse(Boolean isSuccess) {
    HashMap<String, Object> errorResponse = new HashMap<>();
    errorResponse.put(ParseIDToken.ResponseSuccessKey, isSuccess);
    return errorResponse;
  }
}
