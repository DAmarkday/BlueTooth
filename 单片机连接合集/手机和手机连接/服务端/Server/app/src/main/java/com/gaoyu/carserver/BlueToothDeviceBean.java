package com.gaoyu.carserver;

public class BlueToothDeviceBean {
    protected String message;
    //是否配对
    protected boolean isReceive;

    public BlueToothDeviceBean(String msg, boolean isReceive) {
        this.message = msg;
        this.isReceive = isReceive;
    }
}