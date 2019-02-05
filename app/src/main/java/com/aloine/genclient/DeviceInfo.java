package com.aloine.genclient;

import java.util.List;

public class DeviceInfo {
    private String nameOfDevice;
    private String macAddress;
    private List<DeviceInfo> deviceList;

    public String getMacAddress() {
        return macAddress;
    }

    public String getNameOfDevice() {
        return nameOfDevice;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setNameOfDevice(String nameOfDevice) {
        this.nameOfDevice = nameOfDevice;
    }

    public static List<DeviceInfo> deviceList(List<DeviceInfo> deviceList) {
        return deviceList;

    }

    public List<DeviceInfo> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceInfo> deviceList) {
        this.deviceList = deviceList;
    }
}
