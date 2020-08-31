package com.amplitude.skylab;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.amplitude.skylab.Storage;

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
        String oldValue = sharedPrefs.getString(key, null);;
        sharedPrefs.edit().putString(key, value).apply();
        return oldValue;
    }

    @Override
    public String get(String key) {
        return sharedPrefs.getString(key, null);
    }

    @Override
    public void clear() {
        sharedPrefs.edit().clear().apply();
    }
}
