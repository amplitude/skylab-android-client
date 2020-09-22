package com.amplitude.api;

import android.util.Log;

import com.amplitude.skylab.Skylab;
import com.amplitude.skylab.SkylabContext;

public class AmplitudeSkylabContextProvider implements SkylabContext.Provider {

    private AmplitudeClient amplitudeClient;

    public AmplitudeSkylabContextProvider(AmplitudeClient amplitudeClient) {
        this.amplitudeClient = amplitudeClient;
    }

    /**
     * Returns a SkylabContext based on the device id in Amplitude. This blocks the thread until
     * amplitudeClient is initialized, so it should be called on its own thread.
     */
    @Override
    public SkylabContext get() {
        SkylabContext skylabContext = new SkylabContext();

        long start = System.nanoTime();
        while (!amplitudeClient.initialized) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // pass
            }
        }
        long end = System.nanoTime();
        Log.d(Skylab.TAG, String.format("Waited %.3f ms for Amplitude SDK initialization",
                (end - start) / 1000000.0));
        skylabContext.setDeviceId(amplitudeClient.getDeviceId());
        skylabContext.setUserId(amplitudeClient.getUserId());
        return skylabContext;
    }
}
