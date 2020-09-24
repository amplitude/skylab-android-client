package com.amplitude.skylab;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SkylabContext {

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String DEVICE_ID = "device_id";

    String id;
    String userId;
    String deviceId;

    public SkylabContext(String id, String userId, String deviceId) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
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
            object.put(ID, id);
            object.put(USER_ID, userId);
            object.put(DEVICE_ID, deviceId);
        } catch (JSONException e) {
            Log.w(Skylab.TAG, "Error converting SkylabContext to JSONObject", e);
        }
        return object;
    }

    public static SkylabContext.Builder builder() {
        return new SkylabContext.Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String deviceId;

        /**
         * Sets the id. This is the default for determining variation enrollment.
         * If this is null or an empty string, a locally-persisted unique identifier
         * will be used when fetching flags instead.
         * @param id
         * @return this
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the user id on the SkylabContext for connecting with Amplitude's identity
         * @param userId
         * @return
         */
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Sets the device id on the SkylabContext for connecting with Amplitude's identity
         * @param deviceId
         * @return
         */
        public Builder setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public SkylabContext build() {
            return new SkylabContext(id, userId, deviceId);
        }
    }

}
