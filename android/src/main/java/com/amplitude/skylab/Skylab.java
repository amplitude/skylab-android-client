package com.amplitude.skylab;

import android.app.Application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import okhttp3.OkHttpClient;

/**
 * Factory class for Skylab. Manages a pool of singleton instances of SkylabClient identified by
 * name. SkylabClient names are normalized to be lowercase. The name "$default_instance" is
 * reserved for the default SkylabClient instance, and is provided for convenience.
 * <p>
 * All SkylabClients share the same executor service and http client.
 */
public class Skylab {

    public static final String TAG = "Skylab";
    public static final String DEFAULT_VARIANT = "false";
    public static final String DEFAULT_INSTANCE = "$default_instance";

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
    private static final Map<String, SkylabClientImpl> INSTANCES = new ConcurrentHashMap<String,
            SkylabClientImpl>();

    // Public API

    /**
     * Returns the default SkylabClient instance
     */
    public static SkylabClient getInstance() {
        return getInstance(DEFAULT_INSTANCE);
    }

    /**
     * Returns the SkylabClient instance associated with the provided name. If no SkylabClient
     * was initialized with the given name, returns null.
     */
    public static SkylabClient getInstance(String name) {
        String normalizedName = Utils.normalizeInstanceName(name);
        return INSTANCES.get(normalizedName);
    }

    /**
     * Initializes a SkylabClient with the given name. If a SkylabClient already exists with the
     * provided name, returns that instance instead.
     */
    public static synchronized SkylabClient init(Application application,
                                                 String apiKey, SkylabConfig config) {
        return init(DEFAULT_INSTANCE, application, apiKey, config);
    }

    /**
     * Initializes a SkylabClient with associated with the provided name. If a SkylabClient
     * already exists with the provided name, returns that instance instead.
     */
    public static synchronized SkylabClient init(String name, Application application,
                                                 String apiKey,
                                                 SkylabConfig config) {
        String normalizedName = Utils.normalizeInstanceName(name);
        SkylabClientImpl client = INSTANCES.get(normalizedName);
        if (client == null) {
            client = new SkylabClientImpl(application, apiKey, config,
                    new SharedPrefsStorage(application, apiKey), HTTP_CLIENT, EXECUTOR_SERVICE);
            INSTANCES.put(normalizedName, client);
        }
        return client;
    }

    /**
     * Shuts down all SkylabClient instances.
     */
    public static void shutdown() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
    }
}
