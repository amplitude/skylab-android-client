package com.amplitude.skylab;

import android.util.Log;

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
        Object payload = null;
        if (variantJsonObj.has("payload")) {
            payload = variantJsonObj.get("payload");
        }
        return new Variant(key, payload);
    }

    public String toJson() {
        // create a JSONObject and then serialize it
        JSONObject jsonObj = new JSONObject();
        try {
            if (key != null) {
                jsonObj.put("key", key);
            }
            if (payload != null) {
                jsonObj.put("payload", payload);
            }
        } catch (JSONException e) {
            Log.w(Skylab.TAG, "Error converting Variant to json string", e);
        }

        return jsonObj.toString();
    }

    public static Variant fromJson(String json) {
        // deserialize into a JSONObject and then create a Variant
        if (json == null) {
            return null;
        }

        try {
            JSONObject jsonObj = new JSONObject(json);
            return Variant.fromJsonObject(jsonObj);
        } catch (JSONException e) {
            // values persisted in older versions would throw a JSONException
            return new Variant(json);
        }
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
