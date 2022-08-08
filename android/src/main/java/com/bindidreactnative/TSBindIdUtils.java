package com.bindidreactnative;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.Iterator;

public class TSBindIdUtils {

  private static ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

  public static <T> String objectToJson(T obj) throws JsonProcessingException {
    String json = ow.writeValueAsString(obj);
    Log.d("REACT NATIVE JSON", json);
    return json;
  }

  public static <T> JSONObject objectToJSONObject(T obj) throws JSONException, JsonProcessingException {
    return new JSONObject(objectToJson(obj));
  }

  public static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
    WritableMap map = new WritableNativeMap();

    Iterator<String> iterator = jsonObject.keys();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof JSONObject) {
        map.putMap(key, convertJsonToMap((JSONObject) value));
      } else if (value instanceof  JSONArray) {
        map.putArray(key, convertJsonToArray((JSONArray) value));
      } else if (value instanceof  Boolean) {
        map.putBoolean(key, (Boolean) value);
      } else if (value instanceof  Integer) {
        map.putInt(key, (Integer) value);
      } else if (value instanceof  Double) {
        map.putDouble(key, (Double) value);
      } else if (value instanceof String)  {
        map.putString(key, (String) value);
      } else {
        map.putString(key, value.toString());
      }
    }
    return map;
  }

  public static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
    WritableArray array = new WritableNativeArray();

    for (int i = 0; i < jsonArray.length(); i++) {
      Object value = jsonArray.get(i);
      if (value instanceof JSONObject) {
        array.pushMap(convertJsonToMap((JSONObject) value));
      } else if (value instanceof  JSONArray) {
        array.pushArray(convertJsonToArray((JSONArray) value));
      } else if (value instanceof  Boolean) {
        array.pushBoolean((Boolean) value);
      } else if (value instanceof  Integer) {
        array.pushInt((Integer) value);
      } else if (value instanceof  Double) {
        array.pushDouble((Double) value);
      } else if (value instanceof String)  {
        array.pushString((String) value);
      } else {
        array.pushString(value.toString());
      }
    }
    return array;
  }

  public static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
    JSONObject object = new JSONObject();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      switch (readableMap.getType(key)) {
        case Null:
          object.put(key, JSONObject.NULL);
          break;
        case Boolean:
          object.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          object.put(key, readableMap.getDouble(key));
          break;
        case String:
          object.put(key, readableMap.getString(key));
          break;
        case Map:
          object.put(key, convertMapToJson(readableMap.getMap(key)));
          break;
        case Array:
          object.put(key, convertArrayToJson(readableMap.getArray(key)));
          break;
      }
    }
    return object;
  }

  public static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
    JSONArray array = new JSONArray();
    for (int i = 0; i < readableArray.size(); i++) {
      switch (readableArray.getType(i)) {
        case Null:
          break;
        case Boolean:
          array.put(readableArray.getBoolean(i));
          break;
        case Number:
          array.put(readableArray.getDouble(i));
          break;
        case String:
          array.put(readableArray.getString(i));
          break;
        case Map:
          array.put(convertMapToJson(readableArray.getMap(i)));
          break;
        case Array:
          array.put(convertArrayToJson(readableArray.getArray(i)));
          break;
      }
    }
    return array;
  }


  public static JsonObject reactToJSON(ReadableMap readableMap) throws JSONException {
    JsonObject jsonObject = new JsonObject();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while(iterator.hasNextKey()){
      String key = iterator.nextKey();
      ReadableType valueType = readableMap.getType(key);
      switch (valueType){
        case Null:
          jsonObject.add(key, null);
          break;
        case Boolean:
          jsonObject.addProperty(key, readableMap.getBoolean(key));
          break;
        case Number:
          try {
            jsonObject.addProperty(key, readableMap.getInt(key));
          } catch(Exception e) {
            jsonObject.addProperty(key, readableMap.getDouble(key));
          }
          break;
        case String:
          jsonObject.addProperty(key, readableMap.getString(key));
          break;
        case Map:
          jsonObject.add(key, reactToJSON(readableMap.getMap(key)));
          break;
        case Array:
          jsonObject.add(key, reactToJSON(readableMap.getArray(key)));
          break;
      }
    }

    return jsonObject;
  }

  public static JsonArray reactToJSON(ReadableArray readableArray) throws JSONException {
    JsonArray jsonArray = new JsonArray();
    for(int i=0; i < readableArray.size(); i++) {
      ReadableType valueType = readableArray.getType(i);
      switch (valueType){
        case Null:
          jsonArray.add((JsonElement) JSONObject.NULL);
          break;
        case Boolean:
          jsonArray.add(readableArray.getBoolean(i));
          break;
        case Number:
          try {
            jsonArray.add(readableArray.getInt(i));
          } catch(Exception e) {
            jsonArray.add(readableArray.getDouble(i));
          }
          break;
        case String:
          jsonArray.add(readableArray.getString(i));
          break;
        case Map:
          jsonArray.add(reactToJSON(readableArray.getMap(i)));
          break;
        case Array:
          jsonArray.add(reactToJSON(readableArray.getArray(i)));
          break;
      }
    }
    return jsonArray;
  }

  public static WritableMap jsonToReact(JsonObject jsonObject) throws JSONException {
    WritableMap map = Arguments.createMap();

    Iterator<String> iterator = jsonObject.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof JsonObject) {
        map.putMap(key, jsonToReact((JsonObject) value));
      } else if (value instanceof JsonArray) {
        map.putArray(key, jsonToReact((JsonArray) value));
      } else if (value instanceof  Boolean) {
        map.putBoolean(key, (Boolean) value);
      } else if (value instanceof  Integer) {
        map.putInt(key, (Integer) value);
      } else if (value instanceof  Double) {
        map.putDouble(key, (Double) value);
      } else if (value instanceof String)  {
        map.putString(key, (String) value);
      } else {
        map.putString(key, value.toString());
      }
    }
    return map;
  }

  public static WritableArray jsonToReact(JsonArray jsonArray) throws JSONException {
    WritableArray array = Arguments.createArray();

    for (int i = 0; i < jsonArray.size(); i++) {
      Object value = jsonArray.get(i);
      if (value instanceof JsonObject) {
        array.pushMap(jsonToReact((JsonObject) value));
      } else if (value instanceof  JsonArray) {
        array.pushArray(jsonToReact((JsonArray) value));
      } else if (value instanceof  Boolean) {
        array.pushBoolean((Boolean) value);
      } else if (value instanceof  Integer) {
        array.pushInt((Integer) value);
      } else if (value instanceof  Double) {
        array.pushDouble((Double) value);
      } else if (value instanceof String)  {
        array.pushString((String) value);
      } else {
        array.pushString(value.toString());
      }
    }
    return array;
  }

  public static <E extends Enum<E>> E getEnum(ReadableMap map, String key, Class<E> enumClass) {
    try {
      if (map.hasKey(key)) {
        Integer index = map.getInt(key);
        for (E en : EnumSet.allOf(enumClass)) {
          if (Enum.valueOf(enumClass, en.name()).ordinal() == index) {
            return en;
          }
        }
      }

    } catch (IllegalArgumentException e) {
      return null;
    }

    return null;
  }

}
