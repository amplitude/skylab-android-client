package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;

/**
 * Factory for SkylabClients
 */
public class Skylab {

    static final Logger LOGGER = Logger.getLogger(Skylab.class.getName());
    static final Map<String, SkylabClientImpl> INSTANCES = new ConcurrentHashMap<String,
            SkylabClientImpl>();
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    static final ThreadFactory DAEMON_THREAD_FACTORY = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    };
    static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(0, DAEMON_THREAD_FACTORY);

    public static synchronized SkylabClient init(String name, String apiKey, SkylabConfig config) {
        if (!INSTANCES.containsKey(name)) {
            SkylabClientImpl client = new SkylabClientImpl(apiKey, config, HTTP_CLIENT,
                    EXECUTOR_SERVICE,
                    new InMemoryStorage());
            INSTANCES.put(name, client);
        }
        return INSTANCES.get(name);
    }

    public static SkylabClient get(String name) {
        return INSTANCES.get(name);
    }

    public static void shutdown() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SkylabConfig config = SkylabConfig.builder().setPollIntervalSecs(10).build();
        SkylabClient client = Skylab.init("default", "sdk-DYRDKIFIsoJdA3cCDM2VMfq0YwIZpq4J",
                config);
        Future<SkylabClient> future = client.start(new SkylabContext().setUserId("test-user"));
        future.get();
        client.startPolling();
        LOGGER.info("Test User: " + client.getVariant("new-notifications"));

        future = client.setContext(new SkylabContext().setUserId("new-user"));

        future.get();
        LOGGER.info("New User: " + client.getVariant("new-notifications"));
        Skylab.shutdown();
    }
}
