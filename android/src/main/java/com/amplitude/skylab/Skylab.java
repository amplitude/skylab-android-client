package com.amplitude.skylab;

import android.app.Application;

public class Skylab extends SkylabFactory {

    public static String TAG = "Skylab";
    public static String DEFAULT_VARIANT = "false";

    public static synchronized SkylabClient init(String name, Application application,
                                                 String apiKey, SkylabConfig config) {
        return SkylabFactory.init(name, application, apiKey, config,
                new SharedPrefsStorage(application, apiKey));
    }
}
