package com.example.noitavonnetask;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SharedPreference {
    static final String PREF_EMAIL= "Email";
    static final String PREF_PASSWORD= "Password";
    static final String mapKey = "map";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void register(Context ctx, String email, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }

    public static void saveMap(Context ctx, Map<String, String> inputMap){
        JSONObject jsonObject = new JSONObject(inputMap);
        String jsonString = jsonObject.toString();
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(mapKey).apply();
        editor.putString(mapKey, jsonString);
        editor.apply();
    }

    public static void editValue(Context context, String key, String value) throws JSONException {
        Map<String, String> map = getMap(context);
        map.put(key, value);
        saveMap(context, map);
    }

    public static void deleteValue(Context context, String key) throws JSONException {
        Map<String, String> map = getMap(context);
        map.remove(key);
        saveMap(context, map);
    }

    public static Map<String, String> getMap(Context ctx) throws JSONException {
        Map<String, String> outputMap = new HashMap<>();
        String jsonString = getSharedPreferences(ctx).getString(mapKey, (new JSONObject()).toString());
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keysItr = jsonObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            outputMap.put(key, jsonObject.getString(key));
        }
        return outputMap;
    }

    public static boolean login(Context ctx, String email, String password){
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "").equals(email) && getSharedPreferences(ctx).getString(PREF_PASSWORD, "").equals(password);
    }

    public static void clearData(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(mapKey).apply(); //clear all stored data
    }
}
