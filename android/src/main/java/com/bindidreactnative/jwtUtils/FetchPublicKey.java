package com.bindidreactnative.jwtUtils;

import android.util.Log;

import com.bindidreactnative.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchPublicKey {

  public interface fetchBindIDPublicKeyListener {
    void onResponse(String publicKey);
    void onFailure(String error);
  }

  /**
   * Fetch the public key from the BindID jwks endpoint
   *
   * @param listener
   */
  public static void fetchPublicKey(String host, fetchBindIDPublicKeyListener listener) {

    OkHttpClient client = new OkHttpClient();

    String url = "https://" + host + "/jwks";
    Request request = new Request.Builder()
      .url(url)
      .build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, final Response response) throws IOException {
        if (!response.isSuccessful()) {
          Log.e("ERROR REACT-NATIVE", response.toString());
          listener.onFailure("Unexpected code " + response);
        } else {
          // Serialize the response and convert it to an array of key objects
          String responseData = Objects.requireNonNull(response.body()).string();
          try {
            JSONObject json = new JSONObject(responseData);
            JSONArray keys = json.has("keys") ? json.getJSONArray("keys") : null;

            // Find the key that contains the "sig" value in the "use" key. Return the publicKey in it
            for (int i = 0; i < keys.length(); i++) {
              JSONObject key = keys.getJSONObject(i);
              if (key.get("use").equals("sig")) {
                Log.d("REACT-NATIVE", key.toString());
                listener.onResponse(key.toString());
                return;
              }
            }
            Log.e("ERROR REACT-NATIVE", "No signature key in publicKey");
            listener.onFailure("No signature key in publicKey");

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

}
