package com.amplitude.skylab;

import org.json.JSONException;
import org.json.JSONObject;

public class Variant {
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
        Object payload = variantJsonObj.get("payload");
        return new Variant(key, payload);
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
