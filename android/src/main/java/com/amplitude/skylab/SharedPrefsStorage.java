package com.amplitude.skylab;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple SharedPrefs backed storage for caching assigned variant values locally.
 *
 * This is not multi-process safe.
 */
public class SharedPrefsStorage implements Storage {

    Context appContext;
    String sharedPrefsKey;
    SharedPreferences sharedPrefs;

    public SharedPrefsStorage(Context appContext, String instanceName) {
        this.appContext = appContext;
        if (TextUtils.isEmpty(instanceName)) {
            this.sharedPrefsKey = SkylabConfig.SHARED_PREFS_STORAGE_NAME;
        } else {
            this.sharedPrefsKey = SkylabConfig.SHARED_PREFS_STORAGE_NAME + "-" + instanceName;
        }
        sharedPrefs = appContext.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);
    }

    @Override
    public Variant put(String key, Variant value) {
        String oldValue = sharedPrefs.getString(key, null);
        sharedPrefs.edit().putString(key, value.toJson()).apply();
        return Variant.fromJson(oldValue);
    }

    @Override
    public Variant get(String key) {
        return Variant.fromJson(sharedPrefs.getString(key, null));
    }

    @Override
    public Map<String, Variant> getAll() {
        Map<String, Variant> all = new HashMap<>();
        for(Map.Entry<String,?> entry : sharedPrefs.getAll().entrySet()){
            if (entry.getValue() instanceof String) {
                all.put(entry.getKey(), Variant.fromJson((String) entry.getValue()));
            }
        }
        return all;
    }

    @Override
    public void clear() {
        sharedPrefs.edit().clear().apply();
    }
}
