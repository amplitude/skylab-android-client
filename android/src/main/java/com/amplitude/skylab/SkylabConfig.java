package com.amplitude.skylab;

public class SkylabConfig {

    /**
     * This is the common SharedPreferences name from which all SkylabClient instances can share
     * information
     */
    public static final String SHARED_PREFS_SHARED_NAME = "amplitude-flags-shared";

    public static final String SHARED_PREFS_STORAGE_NAME = "amplitude-flags-saved";
    public static final String ENROLLMENT_ID_KEY = "enrollmentId";

    private static final String DEFAULT_SERVER_URL = "https://api.lab.amplitude.com/";
    private static final int DEFAULT_POLL_INTERVAL_SECS = 60 * 10;

    private String serverUrl;
    private int pollIntervalSecs;

    private SkylabConfig(String serverUrl, int pollIntervalSecs) {
        this.serverUrl = serverUrl;
        this.pollIntervalSecs = pollIntervalSecs;
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
        private String serverUrl = DEFAULT_SERVER_URL;
        private int pollIntervalSecs = DEFAULT_POLL_INTERVAL_SECS;

        public SkylabConfig build() {
            return new SkylabConfig(serverUrl, pollIntervalSecs);
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
    }
}
