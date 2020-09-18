package com.amplitude.skylab;

import com.amplitude.api.AmplitudeClient;
import com.amplitude.api.Identify;
import com.amplitude.skylab.SkylabContext;
import com.amplitude.skylab.SkylabListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmplitudeSkylabListener implements SkylabListener {

    AmplitudeClient amplitude;

    public AmplitudeSkylabListener(AmplitudeClient amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void onVariantsChanged(SkylabContext context, Map<String, String> variants) {
        Identify identify = new Identify();
        for (Map.Entry<String, String> entry : variants.entrySet()) {
            identify.set("[Amplitude] [Flag] " + entry.getKey(), entry.getValue());
        }
        amplitude.identify(identify);
    }
}
