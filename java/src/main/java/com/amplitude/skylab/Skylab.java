package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;

/**
 * Factory for SkylabClients
 */
public class Skylab {

    static final Logger LOGGER = Logger.getLogger(Skylab.class.getName());
    static final Map<String, SkylabClientImpl> INSTANCES = new ConcurrentHashMap<String, SkylabClientImpl>();
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(0);

    public static synchronized SkylabClient init(String name, String apiKey, SkylabConfig config) {
        if (!INSTANCES.containsKey(name)) {
            SkylabClientImpl client = new SkylabClientImpl(apiKey, config, HTTP_CLIENT, EXECUTOR_SERVICE,
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

    public static void main(String[] args) {
        // SkylabClient client = new SkylabClient("sdk-lO4W8l0kY4Cn1FkN966Mu51e1x94Qmk3");
        SkylabConfig config = SkylabConfig.builder().setServerUrl("http://localhost:3033/").build();
        SkylabClient client = Skylab.init("default", "sdk-xE8RLkvVTbYrG3VW56t7yyrrK2cUzYdN", config);
        client.identify("skylab");
        client.init(200);

        LOGGER.info(client.getVariant("button-color"));
        Skylab.shutdown();
    }
}
