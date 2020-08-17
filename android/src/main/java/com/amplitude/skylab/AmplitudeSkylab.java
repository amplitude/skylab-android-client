package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class AmplitudeSkylab {
    static final Map<String, SkylabClient> INSTANCES = new ConcurrentHashMap<String, SkylabClient>();

    public static synchronized SkylabClient init(String name, String apiKey, SkylabConfig config) {
        if (!INSTANCES.containsKey(name)) {
            // TODO: plug in Android specific storage
            SkylabClient client = new SkylabClientImpl(apiKey, config, Skylab.HTTP_CLIENT, Skylab.EXECUTOR_SERVICE,
                    new InMemoryStorage());
            // TODO: implement Android Application lifecycle hooks
            INSTANCES.put(name, client);
        }
        return INSTANCES.get(name);
    }

    public static SkylabClient get(String name) {
        return INSTANCES.get(name);
    }

    public static void shutdown() {
        Skylab.shutdown();
    }
}
