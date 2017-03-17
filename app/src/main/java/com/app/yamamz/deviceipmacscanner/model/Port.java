package com.app.yamamz.deviceipmacscanner.model;

/**
 * Created by yamamz on 10/2/2016.
 */

public class Port {

    private String port, date, count;




        public Port() {

        }

        public Port(String port, String date, String count) {

            this.port = port;
            this.date = date;
            this.count = count;
        }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}