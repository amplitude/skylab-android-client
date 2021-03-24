package com.amplitude.skylab;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Variant {
    public final String value;
    public final Object payload;

    public Variant(String value) {
        this(value, null);
    }

    public Variant(String value, Object payload) {
        this.value = value;
        this.payload = payload;
    }

    public String value() {
        return this.value;
    }

    public Object payload() {
        return this.payload;
    }

    public static Variant fromJsonObject(JSONObject variantJsonObj) throws JSONException {
        String value;

        if (variantJsonObj.has("value")) {
            value = variantJsonObj.getString("value");
        } else if (variantJsonObj.has("key")) {
            value = variantJsonObj.getString("key");
        } else {
            return null;
        }

        Object payload = null;
        if (variantJsonObj.has("payload")) {
            payload = variantJsonObj.get("payload");
        }
        return new Variant(value, payload);
    }

    public String toJson() {
        // create a JSONObject and then serialize it
        JSONObject jsonObj = new JSONObject();
        try {
            if (value != null) {
                jsonObj.put("value", value);
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
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
