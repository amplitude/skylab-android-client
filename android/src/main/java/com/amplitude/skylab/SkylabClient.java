package com.amplitude.skylab;

import java.util.concurrent.Future;

public interface SkylabClient {

    /**
     * Fetches evaluations with the given SkylabUser. If skylabUser is null, will fetch with an empty
     * SkylabUser.
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
     * Sets the evaluation SkylabUser. Clears the local cache if the SkylabUser has changed and
     * refetches evaluations.
     *
     * @param skylabUser
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> setUser(SkylabUser skylabUser);

    /**
     * Asynchronously refetches evaluations with the stored SkylabUser.
     *
     * @return A future that resolves when the evaluations have been returned by the server
     */
    Future<SkylabClient> refetchAll();

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

    /**
     * Sets an identity provider that enriches the SkylabUser with a user_id and device_id
     * when fetching flags. The enrichment happens at the time a new network request is made.
     * This is used to connect Skylab to Amplitude identities.
     */
    SkylabClient setIdentityProvider(IdentityProvider provider);

    SkylabClient setListener(SkylabListener skylabListener);

}
