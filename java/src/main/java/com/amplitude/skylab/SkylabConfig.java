package com.amplitude.skylab;

public class SkylabConfig {

    private String serverUrl;

    private SkylabConfig(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String serverUrl = "https://skylab-api.staging.amplitude.com/";

        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public SkylabConfig build() {
            return new SkylabConfig(serverUrl);
        }
    }
}
