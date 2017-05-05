package com.app.yamamz.deviceipmacscanner.model;



import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yamamz on 9/27/2016.
 */

public class Device extends RealmObject {




    @PrimaryKey
    private String ipAddress;
    private Integer _id;
    private String deviceName;
    private String macAddress;
    private Integer image;

    private Integer textColorIP;
    private Integer textColorMac;
    private Integer textColorDeviceName;
    private Integer textColorMacVendor;




    public Device(int _id,String ipAddress, String macAddress, String deviceName, int image ,int textColorIP, int textColorMac, int textColorDeviceName, int textColorMacVendor) {

        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this._id=_id;
        this.image=image;
        this.textColorIP=textColorIP;
        this.textColorMac=textColorMac;
        this.textColorDeviceName=textColorDeviceName;
        this.textColorMacVendor=textColorMacVendor;

    }

    public Device() {
        this.ipAddress = String.valueOf("");
        this.macAddress = String.valueOf("");
        this.deviceName = String.valueOf("");


    }


    public Integer getTextColorMacVendor() {
        return textColorMacVendor;
    }

    public void setTextColorMacVendor(Integer textColorMacVendor) {
        this.textColorMacVendor = textColorMacVendor;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getTextColorIP() {
        return textColorIP;
    }

    public void setTextColorIP(Integer textColorIP) {
        this.textColorIP = textColorIP;
    }

    public Integer getTextColorMac() {
        return textColorMac;
    }

    public void setTextColorMac(Integer textColorMac) {
        this.textColorMac = textColorMac;
    }

    public Integer getTextColorDeviceName() {
        return textColorDeviceName;
    }

    public void setTextColorDeviceName(Integer textColorDeviceName) {
        this.textColorDeviceName = textColorDeviceName;
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

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public int get_id(){
        return _id;
    }
    public void set_id(int _id){
        this._id=_id;
    }
}
