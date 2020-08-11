package com.amplitude.skylab;

import java.util.concurrent.Future;

public class AmplitudeSkylabClient implements SkylabClient {

    SkylabClient client;

    public AmplitudeSkylabClient(SkylabClient client) {
        this.client = client;
    }


    @Override
    public Future<SkylabClientImpl> init() {
        return client.init();
    }

    @Override
    public void init(long l) {
        client.init();
    }

    @Override
    public SkylabClient identify(String s) {
        return client.identify(s);
    }

    @Override
    public SkylabClient startPolling() {
        return client.startPolling();
    }

    @Override
    public SkylabClient stopPolling() {
        return client.stopPolling();
    }

    @Override
    public String getVariant(String s) {
        return client.getVariant(s);
    }
}
