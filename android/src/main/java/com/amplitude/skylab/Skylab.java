package com.amplitude.skylab;

import android.app.Application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import okhttp3.OkHttpClient;

/**
 * Factory class for Skylab. Manages a pool of singleton instances of {@link SkylabClient} identified by
 * name. {@link SkylabClient} names are normalized to be lowercase. The name "$default_instance" is
 * reserved for the default {@link SkylabClient} instance, and is provided for convenience.
 * <p>
 * All {@link SkylabClient} share the same executor service and http client.
 */
public class Skylab {

    public static final String TAG = "Skylab";

    private static final ThreadFactory DAEMON_THREAD_FACTORY = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    };
    private static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE =
            new ScheduledThreadPoolExecutor(0
            , DAEMON_THREAD_FACTORY);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final Map<String, DefaultSkylabClient> INSTANCES = new ConcurrentHashMap<String,
            DefaultSkylabClient>();

    // Public API

    /**
     * Returns the default {@link SkylabClient} instance
     */
    public static SkylabClient getInstance() {
        return getInstance(SkylabConfig.Defaults.INSTANCE_NAME);
    }

    /**
     * Returns the {@link SkylabClient} instance associated with the provided name. If no Client
     * was initialized with the given name, returns null.
     */
    public static SkylabClient getInstance(String name) {
        String normalizedName = SkylabConfig.normalizeInstanceName(name);
        return INSTANCES.get(normalizedName);
    }

    /**
     * Initializes a {@link SkylabClient} with the provided api key and {@link SkylabConfig}.
     * If a {@link SkylabClient} already exists with the instanceName set by the {@link SkylabConfig},
     * returns that instance instead.
     *
     * @param application The Android Application context
     * @param apiKey  The API key. This can be found in the Skylab settings and should not be null or empty.
     * @param config see {@link SkylabConfig} for configuration options
     */
    public static synchronized SkylabClient init(Application application,
                                                 String apiKey, SkylabConfig config) {
        String instanceName = config.getInstanceName();
        DefaultSkylabClient client = INSTANCES.get(instanceName);
        if (client == null) {
            client = new DefaultSkylabClient(application, apiKey, config,
                    new SharedPrefsStorage(application, instanceName), HTTP_CLIENT, EXECUTOR_SERVICE);
            INSTANCES.put(instanceName, client);
        }
        return client;
    }

    /**
     * Shuts down all {@link SkylabClient} instances.
     */
    public static void shutdown() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
    }
}
