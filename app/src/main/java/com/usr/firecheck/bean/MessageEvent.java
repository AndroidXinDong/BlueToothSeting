package com.usr.firecheck.bean;

/**
 * create
 * on 2020-03-30 13:12
 * by xinDong
 **/
public class MessageEvent {
    private String message;
    private Boolean isSend;

    public MessageEvent(String message, Boolean isSend) {
        this.message = message;
        this.isSend = isSend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSend() {
        return isSend;
    }

    public void setSend(Boolean send) {
        isSend = send;
    }
}
