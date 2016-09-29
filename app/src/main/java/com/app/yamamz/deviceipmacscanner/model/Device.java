package com.app.yamamz.deviceipmacscanner.model;

/**
 * Created by Admin on 9/27/2016.
 */
public class Device{
    private String ipAddress;
    private String deviceName;
    private String macAddress;

    public Device(String ipAddress, String macAddress, String deviceName) {
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
    }

    public Device() {
        this.ipAddress = String.valueOf("");
        this.macAddress = String.valueOf("");
        this.deviceName = String.valueOf("");
    }
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
