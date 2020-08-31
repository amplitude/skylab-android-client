package com.amplitude.skylab;


import org.json.JSONObject;

import java.util.Objects;

public class SkylabContext {

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
        object.put("id", id);
        return object;
    }

}
