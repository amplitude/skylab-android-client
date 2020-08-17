package com.amplitude.skylab;

import com.amplitude.api.AmplitudeClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AmplitudeSkylabListener implements SkylabListener {
    static final Logger LOGGER = Logger.getLogger(AmplitudeSkylabListener.class.getName());
    static final String EVENT_TYPE = "[Amplitude] Variant";
    static final String FLAG_PROPERTY = "[Amplitude] Flag";
    static final String VARIANT_PROPERTY = "[Amplitude] Variant";

    AmplitudeClient amplitude;
    String onVariantReceivedEventType = EVENT_TYPE;
    String flagProperty = FLAG_PROPERTY;
    String variantProperty = VARIANT_PROPERTY;

    public AmplitudeSkylabListener(AmplitudeClient amplitude) {
        this.amplitude = amplitude;
    }

    public AmplitudeSkylabListener setOnVariantReceivedEventType(String onVariantReceivedEventType) {
        this.onVariantReceivedEventType = onVariantReceivedEventType;
        return this;
    }

    public AmplitudeSkylabListener setVariantProperty(String variantProperty) {
        this.variantProperty = variantProperty;
        return this;
    }

    private AmplitudeSkylabListener setFlagProperty(String flagProperty) {
        this.flagProperty = flagProperty;
        return this;
    }


    @Override
    public void onVariantReceived(String identity, String flagKey, String variant) {
        if (!identity.equals(amplitude.getUserId())) {
            amplitude.setUserId(identity);
        }
        try {
            JSONObject properties = new JSONObject().put(flagProperty, flagKey).put(variantProperty, variant);
            // TODO: only log once
            amplitude.logEvent(onVariantReceivedEventType, properties);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
