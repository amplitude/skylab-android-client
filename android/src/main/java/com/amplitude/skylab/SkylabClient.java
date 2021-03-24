package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Skylab client interface
 */
public interface SkylabClient {

    /**
     * Fetches evaluations with the given {@link SkylabUser}. If skylabUser is null, will fetch with an empty
     * {@link SkylabUser}.
     *
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> start(SkylabUser skylabUser);

    /**
     * Calls `start(skylabUser)` and blocks for timeoutMs
     *
     * @param skylabUser
     * @param timeoutMs
     */
    void start(SkylabUser skylabUser, long timeoutMs);

    /**
     * Sets the evaluation {@link SkylabUser}. Clears the local cache if the {@link SkylabUser} has changed and
     * refetches evaluations.
     *
     * @param skylabUser
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> setUser(SkylabUser skylabUser);

    /**
     * Asynchronously refetches evaluations with the stored {@link SkylabUser}.
     *
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> refetchAll();

    SkylabClient startPolling();

    SkylabClient stopPolling();

    /**
     * Fetches the {@link Variant} for the given flagKey from local storage
     *
     * @param flagKey
     * @return
     */
    Variant getVariant(String flagKey);

    /**
     * Fetches the {@link Variant} for the given flagKey from local storage.
     * If the variant has not been fetched before, returns fallback.
     *
     * @param flagKey
     * @return
     */
    Variant getVariant(String flagKey, Variant fallback);

    /**
     * Fetches all {@link Variant}s in a Map of flagKey to Variant.
     */
    Map<String, Variant> getVariants();

    /**
     * Sets an identity provider that enriches the {@link SkylabUser} with a user_id and device_id
     * when fetching flags. The enrichment happens at the time a new network request is made.
     * This is used to connect Skylab to Amplitude identities. See {@link ContextProvider}.
     */
    SkylabClient setContextProvider(ContextProvider provider);

    /**
     * Sets a listener for enrollment events. See {@link SkylabListener}
     */
    SkylabClient setListener(SkylabListener skylabListener);

}
