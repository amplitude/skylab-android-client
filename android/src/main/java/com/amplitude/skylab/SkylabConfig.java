package com.amplitude.skylab;

import android.text.TextUtils;

/**
 * Configuration options. This is an immutable object that can be created using
 *  * a {@link SkylabConfig.Builder}. Example usage:
 *  *
 *  * {@code SkylabConfig.builder().setServerUrl("https://api.lab.amplitude.com/").build()}
 */
public class SkylabConfig {

    /**
     * Common SharedPreferences name from which all Client instances can share
     * information.
     */
    static final String SHARED_PREFS_SHARED_NAME = "amplitude-flags-shared";

    static final String SHARED_PREFS_STORAGE_NAME = "amplitude-flags-saved";

    /**
     * Defaults for {@link SkylabConfig}
     */
    public static final class Defaults {

        /**
         * null
         */
        public static final Variant FALLBACK_VARIANT = new Variant(null);

        /**
         * ""
         */
        public static final String INSTANCE_NAME = "";

        /**
         * 6000
         */
        public static final int POLL_INTERVAL_SECS = 60 * 10;

        /**
         * "https://api.lab.amplitude.com/"
         */
        public static final String SERVER_URL = "https://api.lab.amplitude.com/";
    }

    private Variant fallbackVariant;
    private String instanceName;
    private int pollIntervalSecs;
    private String serverUrl;

    private SkylabConfig(
            Variant fallbackVariant,
            String instanceName,
            int pollIntervalSecs,
            String serverUrl
    ) {
        this.fallbackVariant = fallbackVariant;
        this.instanceName = instanceName;
        this.pollIntervalSecs = pollIntervalSecs;
        this.serverUrl = serverUrl;
    }

    public Variant getFallbackVariant() {
        return fallbackVariant;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public int getPollIntervalSecs() {
        return pollIntervalSecs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Variant fallbackVariant = Defaults.FALLBACK_VARIANT;
        private String instanceName = Defaults.INSTANCE_NAME;
        private int pollIntervalSecs = Defaults.POLL_INTERVAL_SECS;
        private String serverUrl = Defaults.SERVER_URL;

        public SkylabConfig build() {
            return new SkylabConfig(fallbackVariant, instanceName, pollIntervalSecs, serverUrl);
        }

        /**
         * Sets the fallback variant
         *
         * @param fallbackVariant
         * @return
         */
        public Builder setFallbackVariant(Variant fallbackVariant) {
            this.fallbackVariant = fallbackVariant;
            return this;
        }

        /**
         * Sets the instanceName
         *
         * @param instanceName
         * @return
         */
        public Builder setInstanceName(String instanceName) {
            this.instanceName = normalizeInstanceName(instanceName);
            return this;
        }

        /**
         * Sets the polling interval
         *
         * @param pollIntervalSecs
         * @return
         */
        public Builder setPollIntervalSecs(int pollIntervalSecs) {
            this.pollIntervalSecs = pollIntervalSecs;
            return this;
        }

        /**
         * Sets the server endpoint from which to fetch flags
         *
         * @param serverUrl
         * @return
         */
        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }
    }

    static String normalizeInstanceName(String instance) {
        if (TextUtils.isEmpty(instance)) {
            instance = SkylabConfig.Defaults.INSTANCE_NAME;
        }
        return instance.toLowerCase();
    }
}
