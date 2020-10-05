package com.amplitude.api;

import android.util.Log;

import com.amplitude.skylab.Skylab;
import com.amplitude.skylab.SkylabUser;
import com.amplitude.skylab.SkylabListener;

import java.util.Map;

public class AmplitudeSkylabListener implements SkylabListener {

    AmplitudeClient amplitude;

    public AmplitudeSkylabListener(AmplitudeClient amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void onVariantsChanged(SkylabUser skylabUser, Map<String, String> variants) {
        Log.d(Skylab.TAG, "Variants changed: " + variants.toString());
        Identify identify = new Identify();
        for (Map.Entry<String, String> entry : variants.entrySet()) {
            identify.set("[Amplitude] [Flag] " + entry.getKey(), entry.getValue());
        }
        amplitude.identify(identify);
    }
}
