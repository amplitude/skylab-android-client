package com.amplitude.skylab;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SkylabContext {

    static final Logger LOGGER = Logger.getLogger(SkylabClientImpl.class.getName());

    public static final String USER_ID_KEY = "user_id";

    String userId;

    public SkylabContext() { }

    public SkylabContext setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Two contexts are equal if the JSON representations of the contexts are equal
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
        } catch (JSONException e) {
            LOGGER.log(Level.WARNING, "Error converting SkylabContext to JSONObject", e);
        }
        return object;
    }

}
