package com.gaoyu.carclient;

/**
 * Created by ${高宇} on 17/4/15.
 */

public class BlueToothDeviceBean {
    protected String message;
    //是否配对
    protected boolean isReceive;

    public BlueToothDeviceBean(String msg, boolean isReceive) {
        this.message = msg;
        this.isReceive = isReceive;
    }
}
