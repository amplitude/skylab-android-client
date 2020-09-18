package com.amplitude.skylab;

import java.util.concurrent.Future;

public interface SkylabClient {

    /**
     * Fetches evaluations with the given context. If context is null, will fetch with an empty
     * context.
     *
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> start(SkylabContext context);

    /**
     * Calls `start(context)` and blocks for timeoutMs
     *
     * @param context
     * @param timeoutMs
     */
    void start(SkylabContext context, long timeoutMs);

    /**
     * Sets the evaluation context. Clears the local cache if the context has changed and
     * refetches evaluations.
     *
     * @param context
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> setContext(SkylabContext context);

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