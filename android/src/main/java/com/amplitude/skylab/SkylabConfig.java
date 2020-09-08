package com.amplitude.skylab;

public class SkylabConfig {

    private static final String DEFAULT_SERVER_URL = "https://skylab-api.staging.amplitude.com/";
    private static final int DEFAULT_POLL_INTERVAL_SECS = 60;

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
         * @param serverUrl
         * @return
         */
        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        /**
         * Sets the polling interval
         * @param pollIntervalSecs
         * @return
         */
        public Builder setPollIntervalSecs(int pollIntervalSecs) {
            this.pollIntervalSecs = pollIntervalSecs;
            return this;
        }
    }
}
