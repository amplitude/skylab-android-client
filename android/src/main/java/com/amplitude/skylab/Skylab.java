package com.amplitude.skylab;

import android.app.Application;

public class Skylab extends SkylabFactory {
    public static synchronized SkylabClient init(String name, Application application,
                                                 String apiKey, SkylabConfig config) {
        return SkylabFactory.init(name, apiKey, config,
                new SharedPrefsStorage(application, apiKey));
    }
}
