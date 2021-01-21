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
     * Common SharedPreferences name from which all SkylabClient instances can share
     * information.
     */
    static final String SHARED_PREFS_SHARED_NAME = "amplitude-flags-shared";

    static final String SHARED_PREFS_STORAGE_NAME = "amplitude-flags-saved";
    static final String ENROLLMENT_ID_KEY = "enrollmentId";

    /**
     * Defaults for {@link SkylabConfig}
     */
    public static final class Defaults {
        /**
         * "https://api.lab.amplitude.com/"
         */
        public static final String SERVER_URL = "https://api.lab.amplitude.com/";

        /**
         * 6000
         */
        public static final int POLL_INTERVAL_SECS = 60 * 10;

        /**
         * ""
         */
        public static final Variant FALLBACK_VARIANT = new Variant("");

        /**
         * ""
         */
        public static final String INSTANCE_NAME = "";
    }

    private String serverUrl;
    private int pollIntervalSecs;
    private Variant fallbackVariant;
    private String instanceName;

    private SkylabConfig(
            String serverUrl,
            int pollIntervalSecs,
            Variant fallbackVariant,
            String instanceName
    ) {
        this.serverUrl = serverUrl;
        this.pollIntervalSecs = pollIntervalSecs;
        this.fallbackVariant = fallbackVariant;
        this.instanceName = instanceName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public int getPollIntervalSecs() {
        return pollIntervalSecs;
    }

    public Variant getFallbackVariant() {
        return fallbackVariant;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String serverUrl = Defaults.SERVER_URL;
        private int pollIntervalSecs = Defaults.POLL_INTERVAL_SECS;
        private Variant fallbackVariant = Defaults.FALLBACK_VARIANT;
        private String instanceName = Defaults.INSTANCE_NAME;

        public SkylabConfig build() {
            return new SkylabConfig(serverUrl, pollIntervalSecs, fallbackVariant, instanceName);
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
    }

    static String normalizeInstanceName(String instance) {
        if (TextUtils.isEmpty(instance)) {
            instance = SkylabConfig.Defaults.INSTANCE_NAME;
        }
        return instance.toLowerCase();
    }
}
