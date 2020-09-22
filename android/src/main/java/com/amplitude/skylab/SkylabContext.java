package com.amplitude.skylab;


import android.util.Log;

import com.amplitude.api.AmplitudeClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SkylabContext {

    public static final String USER_ID_KEY = "user_id";
    public static final String DEVICE_ID_KEY = "device_id";

    String userId;
    String deviceId;

    public SkylabContext() {
    }

    public SkylabContext setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public SkylabContext setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    /**
     * Two contexts are equal if the JSON representations of the contexts are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkylabContext other = (SkylabContext) o;
        return this.toJSONObject().equals(other.toJSONObject());
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(USER_ID_KEY, userId);
            object.put(DEVICE_ID_KEY, deviceId);
        } catch (JSONException e) {
            Log.w(Skylab.TAG, "Error converting SkylabContext to JSONObject", e);
        }
        return object;
    }

    public interface Provider {
        public SkylabContext get();
    }

}
