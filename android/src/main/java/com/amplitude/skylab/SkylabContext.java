package com.amplitude.skylab;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkylabContext {

    static final Logger LOGGER = Logger.getLogger(SkylabClientImpl.class.getName());

    String id;

    public SkylabContext() { }

    public SkylabContext setUserId(String userId) {
        this.id = userId;
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
        return id.hashCode();
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
        } catch (JSONException e) {
            LOGGER.log(Level.WARNING, "Error converting SkylabContext to JSONObject", e);
        }
        return object;
    }

}
