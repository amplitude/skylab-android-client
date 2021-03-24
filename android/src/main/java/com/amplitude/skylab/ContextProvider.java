package com.amplitude.skylab;

/**
 *  Classes can implement this interface to provide a User ID and Device ID to the
 *  {@link SkylabUser} context object. This allows for connecting with Amplitude's
 *  identity system.
 *
 *  Also see {@link SkylabClient#setContextProvider(ContextProvider)}
 */
public interface ContextProvider {

    public String getUserId();

    public String getDeviceId();

    public String getPlatform();

    public String getVersion();

    public String getLanguage();

    public String getOs();

    public String getBrand();

    public String getManufacturer();

    public String getModel();

    public String getCarrier();

}
