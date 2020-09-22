package com.amplitude.exampleapp;

import android.app.Application;

import com.amplitude.api.Amplitude;
import com.amplitude.api.AmplitudeClient;
import com.amplitude.api.AmplitudeSkylabContextProvider;
import com.amplitude.api.AmplitudeSkylabListener;
import com.amplitude.skylab.Skylab;
import com.amplitude.skylab.SkylabClient;
import com.amplitude.skylab.SkylabConfig;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // add the following
        String clientName = "main";
        String apiKey = "client-IAxMYws9vVQESrrK88aTcToyqMxiiJoR";
        SkylabConfig config = SkylabConfig.builder().setServerUrl("https://api.lab.amplitude.com").setPollIntervalSecs(10).build();
        SkylabClient client = Skylab.init(clientName, this, apiKey, config);
        AmplitudeClient amplitude = Amplitude.getInstance();
        amplitude.initialize(this, "a6dd847b9d2f03c816d4f3f8458cdc1d");
        amplitude.setUserId("test-user");
//        client.setListener(new AmplitudeSkylabListener(amplitude));
        client.start(new AmplitudeSkylabContextProvider(amplitude));
    }


}
