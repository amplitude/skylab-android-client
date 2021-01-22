package com.amplitude.skylab;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Variant {
    private static final Gson gson = new Gson();

    public final String key;
    public final Object payload;

    public Variant(String key) {
        this(key, null);
    }

    public Variant(String key, Object payload) {
        this.key = key;
        this.payload = payload;
    }

    public static Variant fromJsonObject(JSONObject variantJsonObj) throws JSONException {
        if (!variantJsonObj.has("key")) {
            return null;
        }

        String key = variantJsonObj.getString("key");
        Object payload = null;
        if (variantJsonObj.has("payload")) {
            payload = variantJsonObj.get("payload");
        }
        return new Variant(key, payload);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Variant fromJson(String json) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, Variant.class);
    }

    /**
     * See {@code #equals(Object)}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    /**
     * Variants are equal if their keys are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Variant other = (Variant) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
