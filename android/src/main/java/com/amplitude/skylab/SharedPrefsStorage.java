package com.amplitude.skylab;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefsStorage implements Storage {

    Context appContext;
    String sharedPrefsKey;
    SharedPreferences sharedPrefs;

    public SharedPrefsStorage(Context appContext, String apiKey) {
        this.appContext = appContext;
        this.sharedPrefsKey = "amplitude." + apiKey + ".flags";
        sharedPrefs = appContext.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);
    }

    @Override
    public String put(String key, String value) {
        String oldValue = sharedPrefs.getString(key, null);
        sharedPrefs.edit().putString(key, value).apply();
        return oldValue;
    }

    @Override
    public String get(String key) {
        return sharedPrefs.getString(key, null);
    }

    @Override
    public Map<String, String> getAll() {
        Map<String, String> all = new HashMap<>();
        for(Map.Entry<String,?> entry : sharedPrefs.getAll().entrySet()){
            if (entry.getValue() instanceof String) {
                all.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return all;
    }

    @Override
    public void clear() {
        sharedPrefs.edit().clear().apply();
    }
}
