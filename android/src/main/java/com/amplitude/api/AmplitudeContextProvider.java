package com.amplitude.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amplitude.skylab.ContextProvider;
import com.amplitude.skylab.Skylab;

import java.util.Locale;

public class AmplitudeContextProvider implements ContextProvider {

    private AmplitudeClient amplitudeClient;
    private boolean initialized;
    private String version;
    private String carrier;

    public static final String PLATFORM = Constants.PLATFORM;
    public static final String OS_NAME = DeviceInfo.OS_NAME;

    public AmplitudeContextProvider(AmplitudeClient amplitudeClient) {
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
        cacheVersion();
        cacheCarrier();
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

    @Override
    public String getPlatform() {
        return PLATFORM;
    }

    private void cacheVersion() {
        if (amplitudeClient.context != null) {
            PackageInfo packageInfo;
            try {
                packageInfo = amplitudeClient.context.getPackageManager().getPackageInfo(amplitudeClient.context.getPackageName(), 0);
                version = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {

            } catch (Exception e) {

            }
        }
    }

    private void cacheCarrier() {
        if (amplitudeClient.context != null) {
            try {
                TelephonyManager manager = (TelephonyManager) amplitudeClient.context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                carrier = manager.getNetworkOperatorName();
            } catch (Exception e) {
                // Failed to get network operator name from network
            }
        }
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    @Override
    public String getOs() {
        return OS_NAME + " " + Build.VERSION.RELEASE;
    }

    @Override
    public String getBrand() {
        return Build.BRAND;
    }

    @Override
    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    @Override
    public String getModel() {
        return Build.MODEL;
    }

    @Override
    public String getCarrier() {
        return carrier;
    }
}
