package com.amplitude.skylab;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * The Skylab user context object. This is an immutable object that can be created using
 * a {@link SkylabUser.Builder}. Example usage:
 *
 * {@code SkylabUser.builder().setLanguage("en").withDetectedPlatform().build()}
 */
public class SkylabUser {

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String DEVICE_ID = "device_id";
    public static final String COUNTRY = "country";
    public static final String REGION = "region";
    public static final String CITY = "city";
    public static final String LANGUAGE = "language";
    public static final String PLATFORM = "platform";
    public static final String VERSION = "version";

    public static final String ANDROID_PLATFORM = "Android";

    String id;
    String userId;
    String deviceId;
    String country;
    String region;
    String city;
    String language;
    String platform;
    String version;
    JSONObject userProperties;

    public SkylabUser(String id, String userId, String deviceId, JSONObject userProperties,
                      String country, String region, String city, String language,
                      String platform, String version) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
        this.userProperties = userProperties;
        this.country = country;
        this.region = region;
        this.city = city;
        this.language = language;
        this.platform = platform;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getLanguage() {
        return language;
    }

    public String getPlatform() {
        return platform;
    }

    public String getVersion() {
        return version;
    }

    public JSONObject getUserProperties() {
        return userProperties;
    }

    /**
     * Two contexts are equal if the JSON representations of the users are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkylabUser other = (SkylabUser) o;
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
            object.put("user_properties", userProperties);
            object.put(COUNTRY, country);
            object.put(REGION, region);
            object.put(CITY, city);
            object.put(LANGUAGE, language);
            object.put(PLATFORM, platform);
            object.put(VERSION, version);
        } catch (JSONException e) {
            Log.w(Skylab.TAG, "Error converting SkylabUser to JSONObject", e);
        }
        return object;
    }

    public static SkylabUser.Builder builder() {
        return new SkylabUser.Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String deviceId;
        private String country;
        private String region;
        private String city;
        private String language;
        private String platform;
        private String version;
        private JSONObject userProperties = new JSONObject();

        /**
         * Sets the id. This is the default for determining variation enrollment.
         * If this is null or an empty string, a locally-persisted unique identifier
         * will be used when fetching flags instead.
         *
         * @param id
         * @return this
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the user id on the SkylabUser for connecting with Amplitude's identity
         *
         * @param userId The User ID used in Amplitude
         */
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Sets the device id on the SkylabUser for connecting with Amplitude's identity
         *
         * @param deviceId The Device ID used in Amplitude
         */
        public Builder setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the language to the default locale language.
         */
        public Builder withDetectedLanguage() {
            return setLanguage(Locale.getDefault().getLanguage());
        }

        public Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        /**
         * Sets the platform to {@link SkylabUser#ANDROID_PLATFORM}
         */
        public Builder withDetectedPlatform() {
            return setPlatform(ANDROID_PLATFORM);
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the version to the versionName of the provided Android context.
         */
        public Builder withDetectedVersion(Context context) {
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName()
                        , 0);
                return setVersion(packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {

            } catch (Exception e) {

            }
            return this;
        }

        /**
         * Sets a custom user property for use in rule-based targeting
         */
        public Builder setUserProperty(String property, Object value) {
            try {
                this.userProperties.put(property, value);
            } catch (JSONException e) {
                Log.e(Skylab.TAG, e.toString());
            }
            return this;
        }

        public SkylabUser build() {
            return new SkylabUser(id, userId, deviceId, userProperties, country, region, city,
                    language, platform, version);
        }
    }

}
