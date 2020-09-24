package com.amplitude.api;

import android.util.Log;

import com.amplitude.skylab.IdentityProvider;
import com.amplitude.skylab.Skylab;

public class AmplitudeIdentityProvider implements IdentityProvider {

    private AmplitudeClient amplitudeClient;
    private boolean initialized;

    public AmplitudeIdentityProvider(AmplitudeClient amplitudeClient) {
        this.amplitudeClient = amplitudeClient;
    }

    private void waitForAmplitudeInitialized() {
        if (initialized) {
            return;
        }

        long start = System.nanoTime();
        while (!amplitudeClient.initialized) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // pass
            }
        }
        initialized = true;
        long end = System.nanoTime();
        Log.d(Skylab.TAG, String.format("Waited %.3f ms for Amplitude SDK initialization",
                (end - start) / 1000000.0));
    }


    @Override
    public String getUserId() {
        waitForAmplitudeInitialized();
        return amplitudeClient.getUserId();
    }

    @Override
    public String getDeviceId() {
        waitForAmplitudeInitialized();
        return amplitudeClient.getDeviceId();
    }
}
