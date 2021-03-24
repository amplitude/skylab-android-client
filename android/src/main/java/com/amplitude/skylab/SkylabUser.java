package com.amplitude.skylab;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

/**
 * The Skylab user context object. This is an immutable object that can be created using
 * a {@link SkylabUser.Builder}. Example usage:
 *
 * {@code SkylabUser.builder().setLanguage("en").build()}
 */
public class SkylabUser {

    public static final String USER_ID = "user_id";
    public static final String DEVICE_ID = "device_id";
    public static final String COUNTRY = "country";
    public static final String REGION = "region";
    public static final String DMA = "region";
    public static final String CITY = "city";
    public static final String LANGUAGE = "language";
    public static final String PLATFORM = "platform";
    public static final String VERSION = "version";
    public static final String OS = "os";
    public static final String DEVICE_FAMILY = "device_family";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_MANUFACTURER = "device_manufacturer";
    public static final String DEVICE_BRAND = "device_brand";
    public static final String DEVICE_MODEL = "device_model";
    public static final String CARRIER = "carrier";
    public static final String LIBRARY = "library";
    public static final String USER_PROPERTIES = "user_properties";

    String userId;
    String deviceId;
    String country;
    String region;
    String dma;
    String city;
    String language;
    String platform;
    String version;
    String os;
    String deviceFamily;
    String deviceType;
    String deviceManufacturer;
    String deviceBrand;
    String deviceModel;
    String carrier;
    String library;

    JSONObject userProperties;

    private SkylabUser(String userId, String deviceId, String country, String region, String dma,
                      String city, String language, String platform, String version, String os,
                      String deviceFamily, String deviceType, String deviceManufacturer,
                      String deviceBrand, String deviceModel, String carrier, String library,
                      JSONObject userProperties) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.country = country;
        this.region = region;
        this.dma = dma;
        this.city = city;
        this.language = language;
        this.platform = platform;
        this.version = version;
        this.os = os;
        this.deviceFamily = deviceFamily;
        this.deviceType = deviceType;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.carrier = carrier;
        this.library = library;
        this.userProperties = userProperties;
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

    public String getDma() {
        return dma;
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

    public String getOs() { return os; }

    public String getDeviceFamily() {
        return deviceFamily;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getLibrary() {
        return library;
    }

    public JSONObject getUserProperties() {
        return userProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkylabUser that = (SkylabUser) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (dma != null ? !dma.equals(that.dma) : that.dma != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (platform != null ? !platform.equals(that.platform) : that.platform != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (os != null ? !os.equals(that.os) : that.os != null) return false;
        if (deviceFamily != null ? !deviceFamily.equals(that.deviceFamily) :
                that.deviceFamily != null)
            return false;
        if (deviceType != null ? !deviceType.equals(that.deviceType) : that.deviceType != null)
            return false;
        if (deviceManufacturer != null ? !deviceManufacturer.equals(that.deviceManufacturer) :
                that.deviceManufacturer != null)
            return false;
        if (deviceBrand != null ? !deviceBrand.equals(that.deviceBrand) : that.deviceBrand != null)
            return false;
        if (deviceModel != null ? !deviceModel.equals(that.deviceModel) : that.deviceModel != null)
            return false;
        if (carrier != null ? !carrier.equals(that.carrier) : that.carrier != null) return false;
        if (library != null ? !library.equals(that.library) : that.library != null) return false;
        return userProperties != null ? userProperties.equals(that.userProperties) :
                that.userProperties == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (dma != null ? dma.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (deviceFamily != null ? deviceFamily.hashCode() : 0);
        result = 31 * result + (deviceType != null ? deviceType.hashCode() : 0);
        result = 31 * result + (deviceManufacturer != null ? deviceManufacturer.hashCode() : 0);
        result = 31 * result + (deviceBrand != null ? deviceBrand.hashCode() : 0);
        result = 31 * result + (deviceModel != null ? deviceModel.hashCode() : 0);
        result = 31 * result + (carrier != null ? carrier.hashCode() : 0);
        result = 31 * result + (library != null ? library.hashCode() : 0);
        result = 31 * result + (userProperties != null ? userProperties.hashCode() : 0);
        return result;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(USER_ID, userId);
            object.put(DEVICE_ID, deviceId);
            object.put(USER_PROPERTIES, userProperties);
            object.put(COUNTRY, country);
            object.put(CITY, city);
            object.put(REGION, region);
            object.put(DMA, city);
            object.put(LANGUAGE, language);
            object.put(PLATFORM, platform);
            object.put(VERSION, version);
            object.put(OS, os);
            object.put(DEVICE_FAMILY, deviceFamily);
            object.put(DEVICE_TYPE, deviceType);
            object.put(DEVICE_BRAND, deviceBrand);
            object.put(DEVICE_MANUFACTURER, deviceManufacturer);
            object.put(DEVICE_MODEL, deviceModel);
            object.put(CARRIER, carrier);
            object.put(LIBRARY, library);
        } catch (JSONException e) {
            Log.w(Skylab.TAG, "Error converting SkylabUser to JSONObject", e);
        }
        return object;
    }

    public static SkylabUser.Builder builder() {
        return new SkylabUser.Builder();
    }

    public static class Builder {
        private String userId;
        private String deviceId;
        private String country;
        private String region;
        private String city;
        private String language;
        private String platform;
        private String version;
        private String os;
        private JSONObject userProperties = new JSONObject();
        private String dma;
        private String deviceFamily;
        private String deviceType;
        private String deviceManufacturer;
        private String deviceBrand;
        private String deviceModel;
        private String carrier;
        private String library;

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

        public Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setDma(String dma) {
            this.dma = dma;
            return this;
        }

        public Builder setOs(String os) {
            this.os = os;
            return this;
        }

        public Builder setDeviceFamily(String deviceFamily) {
            this.deviceFamily = deviceFamily;
            return this;
        }

        public Builder setDeviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder setDeviceManufacturer(String deviceManufacturer) {
            this.deviceManufacturer = deviceManufacturer;
            return this;
        }

        public Builder setDeviceBrand(String deviceBrand) {
            this.deviceBrand = deviceBrand;
            return this;
        }

        public Builder setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
            return this;
        }

        public Builder setCarrier(String carrier) {
            this.carrier = carrier;
            return this;
        }

        public Builder setLibrary(String library) {
            this.library = library;
            return this;
        }

        public Builder setUserProperties(JSONObject userProperties) {
            this.userProperties = userProperties;
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
            return new SkylabUser(userId, deviceId, country, region, dma, city, language, platform, version, os, deviceFamily, deviceType, deviceManufacturer, deviceBrand, deviceModel, carrier, library, userProperties);
        }
    }

}
