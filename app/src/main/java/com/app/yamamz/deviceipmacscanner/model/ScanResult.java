package com.app.yamamz.deviceipmacscanner.model;

/**
 * Created by yamamz on 10/3/2016.
 */

public class ScanResult {
    private int port;

    private boolean isOpen;

    public ScanResult(int port, boolean isOpen) {
        super();
        this.port = port;
        this.isOpen = isOpen;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

}
