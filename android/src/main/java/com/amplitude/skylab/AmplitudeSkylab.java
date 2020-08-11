package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class AmplitudeSkylab {
    static final Logger LOGGER = Logger.getLogger(Skylab.class.getName());
    static final Map<String, SkylabClient> INSTANCES = new ConcurrentHashMap<String, SkylabClient>();

    public static synchronized SkylabClient init(String name, String apiKey, SkylabConfig config) {
        if (!INSTANCES.containsKey(name)) {
            SkylabClient client = new AmplitudeSkylabClient(new SkylabClientImpl(apiKey, config, Skylab.HTTP_CLIENT, Skylab.EXECUTOR_SERVICE,
                    new InMemoryStorage()));
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
