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
     *
     * @param flagKey
     * @return
     */
    String getVariant(String flagKey);

    /**
     * Fetches the variant for the given flagKey from local storage.
     * If the variant has not been fetched before, returns fallback.
     *
     * @param flagKey
     * @return
     */
    String getVariant(String flagKey, String fallback);

    SkylabClient setListener(SkylabListener skylabListener);

}
