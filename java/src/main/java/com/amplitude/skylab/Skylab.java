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
        SkylabConfig config = SkylabConfig.builder().setServerUrl("https://skylab-api.staging.amplitude.com/").build();
        SkylabClient client = Skylab.init("default", "sdk-HsmGr5Llyy321hN0ZXIDVU2dDJmlhqfz", config);
        client.identify("load-tester@skylab");
        client.init(5000);

        LOGGER.info(client.getVariant("button-color"));
        Skylab.shutdown();
    }
}
