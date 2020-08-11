package com.amplitude.skylab;

import java.util.concurrent.Future;

public interface SkylabClient {

    Future<SkylabClientImpl> init();

    void init(long timeoutMs);

    SkylabClient identify(String userId);

    SkylabClient startPolling();

    SkylabClient stopPolling();

    /**
     * Fetches the variant for the given flagKey from local storage
     * @param flagKey
     * @return
     */
    String getVariant(String flagKey);

}