package com.app.yamamz.deviceipmacscanner.model;

import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;

/**
 * Created by Admin on 9/27/2016.
 */
@Entity
public class Device {

    @PrimaryKey

    @Column
    private Integer _id;

    @Column
    private String ipAddress;
    @Column
    private String deviceName;

    @Column
    private String macAddress;


    @Column
    private Integer image;


    public Device(int _id,String ipAddress, String macAddress, String deviceName, int image ) {

        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this._id=_id;
        this.image=image;



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
